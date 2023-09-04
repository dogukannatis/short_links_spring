package com.linkshortener.controller;

import com.linkshortener.entity.ERole;
import com.linkshortener.entity.Role;
import com.linkshortener.entity.User;
import com.linkshortener.repository.RoleRepository;
import com.linkshortener.repository.UserRepository;
import com.linkshortener.security.jwt.JwtUtils;
import com.linkshortener.security.services.EmailSenderService;
import com.linkshortener.security.services.UserDetailsImpl;
import com.mongodb.lang.Nullable;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EmailSenderService senderService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${linkshortener.app.url}")
    private String url;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping("/")
    public String home(){
        return "Hello World!";
    }

    @Autowired
    private PasswordEncoder encoder;

    @PreAuthorize("('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyData() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> user = userRepository.findById(userDetails.getId());


        if(user.isPresent()){
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }

    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/me")
    public ResponseEntity<Map<String, Object>> updateUserData(@RequestBody User newUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> user = userRepository.findById(userDetails.getId());

        if(user.isPresent()){
            newUser.setId(userDetails.getId());

            if(!user.get().getEmail().equals(newUser.getEmail())){
                newUser.setEmailVerified(false);

                String jwt = jwtUtils.generateMailJwtToken(
                        user.get().getId(), user.get().getUsername(), user.get().getEmail());

                String verificationLink = url + "/api/auth/verifyEmail/" + jwt;

                senderService.sendEmail(
                        user.get().getEmail(),
                        "Email Verification For Link Shortener App",
                        "Please click following link to verificate your account: " + verificationLink);

            }else{
                newUser.setEmailVerified(true);
            }

            if(newUser.getPassword() != null){
                newUser.setPassword(encoder.encode(newUser.getPassword()));
            }else{
                newUser.setPassword(user.get().getPassword());
            }

            if(newUser.getUsername() != null){
                newUser.setUsername(newUser.getUsername());
            }else{
                newUser.setUsername(user.get().getUsername());
            }



            Set<String> strRoles = newUser.getRoleNames();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);

                            break;
                        case "mod":
                            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);

                            break;
                        default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }

            newUser.setRoles(roles);



            userRepository.save(newUser);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("message", "User has been updated");
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getUserWithId/{id}")
    public ResponseEntity<Map<String, Object>> getUserWithId(@PathVariable final String id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }



    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateUser/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable final String id, @RequestBody User newUser) {
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            newUser.setId(id);
            userRepository.save(newUser);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("message", "User has been updated");
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> user = userRepository.findById(userDetails.getId());

        if(user.isPresent()){
            userRepository.deleteById(userDetails.getId());

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("message", "User has been deleted");
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deleteAllUsers")
    public ResponseEntity<Boolean> deleteAllUsers() {
        userRepository.deleteAll();

        return ResponseEntity.ok(true);
    }







}

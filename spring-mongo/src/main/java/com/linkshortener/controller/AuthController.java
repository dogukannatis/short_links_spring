package com.linkshortener.controller;
import com.linkshortener.entity.ERole;
import com.linkshortener.entity.Role;
import com.linkshortener.entity.User;
import com.linkshortener.payload.request.LoginRequest;
import com.linkshortener.payload.request.SignupRequest;
import com.linkshortener.payload.response.JwtResponse;
import com.linkshortener.payload.response.MessageResponse;
import com.linkshortener.repository.RoleRepository;
import com.linkshortener.repository.UserRepository;
import com.linkshortener.security.jwt.JwtUtils;
import com.linkshortener.security.services.EmailSenderService;
import com.linkshortener.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EmailSenderService senderService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${linkshortener.app.url}")
    private String url;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) { //@Valid

        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());

        if(user.isPresent() && !user.get().isEmailVerified()){
            return ResponseEntity.ok("Please verfiy your email");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
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

        user.setRoles(roles);

        userRepository.save(user);


        String jwt = jwtUtils.generateMailJwtToken(user.getId(), user.getUsername(), user.getEmail());

        String verificationLink = url + "/api/auth/verifyEmail/" + jwt;

        senderService.sendEmail(
                user.getEmail(),
                "Email Verification For Link Shortener App",
                "Please click following link to verificate your account: " + verificationLink);

        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please check your mail box for verification."));
    }


    @GetMapping("/verifyEmail/{token}")
    public ResponseEntity<Boolean> verifyEmail(@PathVariable String token) {

        if(jwtUtils.validateMailJwtToken(token)){
            String userName = jwtUtils.getUserNameFromMailJwtToken(token);

            System.out.println("userName " + userName);


            Optional<User> user = userRepository.findByUsername(userName);

            if(user.isPresent()){
                user.get().setEmailVerified(true);

                userRepository.save(user.get());

                return ResponseEntity.ok(true);
            }else{
                return ResponseEntity.ok(false);
            }


            //Optional<User> user = userRepository.findById(claims.get());


        }else{
            return ResponseEntity.ok(false);
        }


    }


}

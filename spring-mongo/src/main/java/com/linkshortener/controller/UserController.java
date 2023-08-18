package com.linkshortener.controller;

import com.linkshortener.entity.User;
import com.linkshortener.repository.UserRepository;
import com.mongodb.lang.Nullable;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @RequestMapping("/")
    public String home(){
        return "Hello World!";
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyData(@RequestBody String userID) {
        Optional<User> user = userRepository.findById(userID);

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

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/me")
    public ResponseEntity<Map<String, Object>> updateUserData(@RequestBody String userID, @RequestBody User newUser) {
        Optional<User> user = userRepository.findById(userID);

        if(user.isPresent()){
            newUser.setId(userID);
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

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody String userID, String password) {
        Optional<User> user = userRepository.findById(userID);

//Optional<User>
        if(user.isPresent()){
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("message", "User logged in");
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


    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User result = userRepository.save(user);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody String userID) {
        Optional<User> user = userRepository.findById(userID);

        if(user.isPresent()){
            userRepository.deleteById(userID);

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

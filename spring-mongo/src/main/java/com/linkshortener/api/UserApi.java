package com.linkshortener.api;

import com.linkshortener.entity.User;
import com.linkshortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.signIn(user));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyData(@RequestParam String userID) {
        return ResponseEntity.ok(userRepository.getMyData(userID));
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUserData(@RequestParam String userID) {
        return ResponseEntity.ok(userRepository.updateUserData(userID));
    }

    @DeleteMapping("/me")
    public ResponseEntity<User> deleteUserData(@RequestParam String userID) {
        return ResponseEntity.ok(userRepository.updateUserData(userID));
    }

    @PostMapping("/:id")
    public ResponseEntity<User> getUserData(@RequestParam String userID) {
        return ResponseEntity.ok(userRepository.getUserData(userID));
    }

    @PatchMapping("/updateUser/:id")
    public ResponseEntity<User> updateUserData(@RequestParam String userID) {
        return ResponseEntity.ok(userRepository.updateUserData(userID));
    }


}

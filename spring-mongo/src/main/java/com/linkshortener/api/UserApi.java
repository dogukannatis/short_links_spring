package com.linkshortener.api;

import com.linkshortener.controller.UserController;
import com.linkshortener.entity.User;
import com.linkshortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequestMapping("/user")
public class UserApi {


    private UserController userRepository;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @GetMapping("/getUserWithId/{id}")
    public ResponseEntity<Optional<User>> getUserWithId(@PathVariable final String id) {
        return userRepository.getUserWithId(id);
    }

    @PostMapping("/updateUser/{id}")
    public ResponseEntity<Optional<User>> updateUser(@PathVariable final String id, @RequestBody User newUser) {
        return userRepository.updateUser(id, newUser);
    }

    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return userRepository.saveUser(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<Optional<User>> signIn(@RequestBody String userID, String password) {
        return userRepository.signIn(userID, password);
    }




}

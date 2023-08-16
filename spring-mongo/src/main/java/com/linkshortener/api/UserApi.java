package com.linkshortener.api;

import com.linkshortener.controller.UserController;
import com.linkshortener.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequestMapping("/user")
public class UserApi {


    private UserController userController;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return userController.getAllUsers();
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody String userID, String password) {
        return userController.signIn(userID, password);
    }

    @GetMapping("/me")
    public ResponseEntity<Optional<User>> getMyData(@RequestBody String userID) {
        return userController.getMyData(userID);
    }

    @PatchMapping("/me")
    public ResponseEntity<Optional<User>> updateUserData(@RequestBody String userID, @RequestBody User user) {
        return userController.updateUserData(userID, user);
    }

    @GetMapping("/getUserWithId/{id}")
    public ResponseEntity<Optional<User>> getUserWithId(@PathVariable final String id) {
        return userController.getUserWithId(id);
    }

    @PostMapping("/updateUser/{id}")
    public ResponseEntity<Optional<User>> updateUser(@PathVariable final String id, @RequestBody User newUser) {
        return userController.updateUser(id, newUser);
    }

    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return userController.saveUser(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Optional<User>> deleteUser(@RequestBody String userID) {
        return userController.deleteUser(userID);
    }

    @GetMapping("/deleteAllUsers")
    public ResponseEntity<Boolean> deleteAllUsers() {
        return userController.deleteAllUsers();
    }




}

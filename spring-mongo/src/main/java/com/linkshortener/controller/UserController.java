package com.linkshortener.controller;

import com.linkshortener.entity.User;
import com.linkshortener.repository.UserRepository;
import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Optional<User>> getMyData(String userID) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Optional<User>> updateUserData(String userID, User newUser) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));

        if(user.isPresent()){ // of nullable
            newUser.setId(userID);
            userRepository.save(newUser);

            return ResponseEntity.ok().body(user);
        }else{
            return ResponseEntity.status(404).body(user);
        }
    }

    public ResponseEntity<Map<String, Object>> signIn(String userID, String password) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));

//Optional<User>
        if(user.isPresent()){
            Map<String, Object> userMap = new HashMap<String, Object>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("user", user);
            return ResponseEntity.ok(userMap);
        }else{
            //throw new RuntimeException("User not found");
            // return ResponseEntity.status(404).body("User not found");
            //return ResponseEntity.notFound().build();

            Map<String, Object> errorResponse = new HashMap<String, Object>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No user record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }


    }

    public ResponseEntity<Optional<User>> getUserWithId(String userID) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Optional<User>> updateUser(String userID, User newUser) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));

        if(user.isPresent()){ // of nullable
            newUser.setId(userID);
            userRepository.save(newUser);

            return ResponseEntity.ok().body(user);
        }else{
            return ResponseEntity.status(404).body(user);
        }
    }

    public ResponseEntity<User> saveUser(User user) {
        User result = userRepository.save(user);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Optional<User>> deleteUser(String userID) {
        Optional<User> user = userRepository.findById(new ObjectId(userID));

        userRepository.deleteById(new ObjectId(userID));

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Boolean> deleteAllUsers() {
        userRepository.deleteAll();

        return ResponseEntity.ok(true);
    }




}

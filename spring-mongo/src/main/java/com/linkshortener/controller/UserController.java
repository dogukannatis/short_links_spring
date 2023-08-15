package com.linkshortener.controller;

import com.linkshortener.entity.User;
import com.linkshortener.repository.UserRepository;
import com.mongodb.internal.bulk.UpdateRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    public ResponseEntity<Optional<User>> getUserWithId(String id) {
        Optional<User> user = userRepository.findById(new ObjectId(id));
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Optional<User>> updateUser(String id, User newUser) {
        Optional<User> user = userRepository.findById(new ObjectId(id));

        if(user.isPresent()){ // of nullable
            newUser.setId(id);

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

    public ResponseEntity<Optional<User>> signIn(String userID, String password) {
        Optional<User> user = userRepository.findById(userID);

        if(user.isPresent()){
            return ResponseEntity.ok(user);
        }else{
           //throw new RuntimeException("User not found");
            return ResponseEntity.status(404).body("User not found");
        }


    }


}

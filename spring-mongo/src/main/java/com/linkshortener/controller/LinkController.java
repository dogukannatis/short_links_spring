package com.linkshortener.controller;

import com.linkshortener.AppConstants;
import com.linkshortener.entity.Link;

import com.linkshortener.entity.User;
import com.linkshortener.repository.LinkRepository;

import com.linkshortener.repository.UserRepository;
import com.linkshortener.security.services.UserDetailsImpl;
import com.linkshortener.utilities.RandomStringGenerator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

@RestController
@RequestMapping("/api/link")
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllLinks")
    public ResponseEntity<List<Link>> getAllLinks() {
        List<Link> links = linkRepository.findAll();
        return ResponseEntity.ok(links);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getMyLinks")
    public ResponseEntity<List<Link>> getMyLinks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        Query query = new Query();
        query.addCriteria(Criteria.where("belongs_to").is(userDetails.getId()));
        List<Link> links = mongoTemplate.find(query, Link.class);

        return ResponseEntity.ok(links);

    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Link> addLink(@RequestBody Link url) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //byte[] array = new byte[6];
        // new Random().nextBytes(array);
        // String refString = new String(array, Charset.forName("UTF-8"));

        String refString = RandomStringGenerator.generateRandomString(6).toUpperCase();


        Link link = new Link(
                url.getOriginal_link(),
                refString,
                userDetails.getId(),
                0
        );



        Link result = linkRepository.save(link);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteLink(@PathVariable String id) {
        linkRepository.deleteById(id);
        return ResponseEntity.ok(true);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Boolean> deleteAllLinks() {
        linkRepository.deleteAll();
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/deleteMyLink/{id}")
    public ResponseEntity<Map<String, Object>> deleteMyLink(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<User> user = userRepository.findById(userDetails.getId());

        Optional<Link> link = linkRepository.findById(id);


        if(link.isPresent() && link.get().getId().equals(id) && link.get().getBelongs_to().equals(user.get().getId())){
            linkRepository.deleteById(id);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", HttpStatus.OK);
            userMap.put("message", "Link has been deleted");
            userMap.put("link", link);
            return ResponseEntity.ok(userMap);
        }else{

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "error");
            errorResponse.put("message", "No link record found");
            errorResponse.put("status",HttpStatus.NOT_FOUND.toString());

            return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
        }

    }


    @GetMapping("/redirect/{ref}")
    public ResponseEntity<Link> redirect(@PathVariable String ref) throws URISyntaxException {

        Query query = new Query();
        query.addCriteria(Criteria.where("link_ref").is(ref));
        Link link = mongoTemplate.findOne(query, Link.class);
        if(link != null){
            link.setClick(link.getClick() + 1);
            linkRepository.save(link);
        }


        //URI newDirection = new URI(AppConstants.baseUrl+"/redirect/"+ref);
        URI newDirection = new URI(link.getOriginal_link());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(newDirection);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);

    }
}

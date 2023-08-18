package com.linkshortener.controller;

import com.linkshortener.AppConstants;
import com.linkshortener.entity.Link;

import com.linkshortener.repository.LinkRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/link")
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/getAllLinks")
    public ResponseEntity<List<Link>> getAllLinks() {
        List<Link> links = linkRepository.findAll();
        return ResponseEntity.ok(links);
    }
    @GetMapping("/getMyLinks")
    public ResponseEntity<List<Link>> getMyLinks(@RequestBody String userID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("belongs_to").is(userID));
        List<Link> links = mongoTemplate.find(query, Link.class);

        return ResponseEntity.ok(links);

    }
    @PostMapping("/add")
    public ResponseEntity<Link> addLink(@RequestBody Link link) {
        Link result = linkRepository.save(link);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteLink(@PathVariable String id) {
        linkRepository.deleteById(id);
        return ResponseEntity.ok(true);

    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Boolean> deleteAllLinks() {
        linkRepository.deleteAll();
        return ResponseEntity.ok(true);
    }
    @DeleteMapping("/deleteMyLink/{id}")
    public ResponseEntity<Map<String, Object>> deleteMyLink(@RequestBody String userID, @PathVariable String id) {
        Optional<Link> link = linkRepository.findById(id);


        if(link.isPresent() && link.get().getId().equals(id)){
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
    @RequestMapping("/redirect/{ref}")
    public ResponseEntity<Link> redirect(@PathVariable String ref) throws URISyntaxException {
        URI yahoo = new URI(AppConstants.baseUrl+"/redirect/"+ref);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(yahoo);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);

    }
}

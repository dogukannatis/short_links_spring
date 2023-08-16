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
import org.springframework.web.bind.annotation.RestController;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    public ResponseEntity<List<Link>> getAllLinks() {
        List<Link> links = linkRepository.findAll();
        return ResponseEntity.ok(links);
    }

    public ResponseEntity<List<Link>> getMyLinks(String userID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("belongs_to").is(userID));
        List<Link> links = mongoTemplate.find(query, Link.class);

        return ResponseEntity.ok(links);

    }

    public ResponseEntity<Link> addLink(Link link) {
        Link result = linkRepository.save(link);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Boolean> deleteLink(String id) {
        linkRepository.deleteById(new ObjectId(id));

        return ResponseEntity.ok(true);

    }

    public ResponseEntity<Boolean> deleteAllLinks() {
        linkRepository.deleteAll();
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Optional<Link>> deleteMyLink(String userID, String id) {
        Optional<Link> link = linkRepository.findById(new ObjectId(id));
        if(link.isPresent() && link.get().getId().equals(id)){
            linkRepository.deleteById(new ObjectId(id));
            return ResponseEntity.ok(link);
        }

        return null;
    }

    public ResponseEntity<Link> redirect(String ref) throws URISyntaxException {
        URI yahoo = new URI(AppConstants.baseUrl+"/redirect/"+ref);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(yahoo);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);

    }
}

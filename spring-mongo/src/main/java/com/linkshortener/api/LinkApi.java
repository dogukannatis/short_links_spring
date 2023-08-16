package com.linkshortener.api;

import com.linkshortener.controller.LinkController;
import com.linkshortener.entity.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RequestMapping("/link")
public class LinkApi {


    private LinkController linkController;

    @GetMapping("/getAllLinks")
    public ResponseEntity<List<Link>> getAllLinks() {
        return linkController.getAllLinks();
    }

    @GetMapping("/getMyLinks")
    public ResponseEntity<List<Link>> getMyLinks(@RequestBody String userID) {
        return linkController.getMyLinks(userID);
    }

    @PostMapping("/add")
    public ResponseEntity<Link> addLink(@RequestBody Link link) {
        return linkController.addLink(link);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteLink(@PathVariable String id) {
        return linkController.deleteLink(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Boolean> deleteAllLinks() {
        return linkController.deleteAllLinks();
    }

    @DeleteMapping("/deleteMyLink/{id}")
    public ResponseEntity<Optional<Link>> deleteMyLink(@RequestBody String userID, @PathVariable String id) {
        return linkController.deleteMyLink(userID, id);
    }

    @RequestMapping("/redirect/{ref}")
    public ResponseEntity<Link> redirect(@PathVariable String ref) throws URISyntaxException {
        return linkController.redirect(ref);
    }




}

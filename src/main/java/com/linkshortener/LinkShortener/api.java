package com.linkshortener.LinkShortener;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class api {

    @GetMapping
    public String test() {
        return "This is a test message";
    }



}

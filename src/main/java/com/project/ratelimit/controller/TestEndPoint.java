package com.project.ratelimit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestEndPoint {

    @GetMapping("/api")
    public String test() {
        return "Test endpoint is working!";
    }

}

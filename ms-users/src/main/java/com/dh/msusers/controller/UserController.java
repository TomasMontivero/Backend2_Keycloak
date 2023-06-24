package com.dh.msusers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    // URL: http://localhost:8090/api/v2/users/hello
    @GetMapping("/hello")
    public ResponseEntity getHello() {
        return ResponseEntity.ok("Hello");
    }

}

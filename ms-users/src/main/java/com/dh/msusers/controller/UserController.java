package com.dh.msusers.controller;

import com.dh.msusers.model.User;
import com.dh.msusers.model.UserAndBills;
import com.dh.msusers.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/hello")
    public ResponseEntity getHello() {
        return ResponseEntity.ok("Hello Users");
    }


    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/bills/{id}")
    public UserAndBills getUserAndBills(@PathVariable String id) {
        return userService.getUserAndBillsById(id);
    }

}

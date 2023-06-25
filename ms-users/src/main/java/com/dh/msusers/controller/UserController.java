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


    // URL: http://localhost:8090/api/v2/users/hello
    @GetMapping("/hello")
    public ResponseEntity getHello() {
        return ResponseEntity.ok("Hello Users");
    }

    @GetMapping("/hello/bills")
    public ResponseEntity getHelloBills() {
        return userService.getHelloBills();
    }

    // URL: http://localhost:8090/api/v2/users/c14e61fd-9562-41b7-9762-71031248c5b8
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // URL: http://localhost:8090/api/v2/users/bills/c14e61fd-9562-41b7-9762-71031248c5b8
    @GetMapping("/bills/{id}")
    public UserAndBills getUserAndBills(@PathVariable String id) {
        return userService.getUserAndBillsById(id);
    }

}

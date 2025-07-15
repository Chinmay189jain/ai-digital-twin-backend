package com.digitaltwin.backend.controller;


import com.digitaltwin.backend.model.User;
import com.digitaltwin.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/{id}")
    public User getUSerById(@RequestParam String id) {
        return userService.findById(id).orElse(null);
    }
}

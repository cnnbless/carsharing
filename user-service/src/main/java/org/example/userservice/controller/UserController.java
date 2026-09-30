package org.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.userservice.entity.User;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/verify")
    public User verifyLicense(@PathVariable UUID id) {
        return userService.verifyLicense(id);
    }
}
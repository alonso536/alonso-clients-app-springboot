package org.alonso.clientsapp.clientsapp.controllers;

import java.util.List;
import java.util.Optional;

import org.alonso.clientsapp.clientsapp.models.dto.UserDto;
import org.alonso.clientsapp.clientsapp.models.entity.User;
import org.alonso.clientsapp.clientsapp.models.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> index() {
        return userService.findAll();
    }

    @PostMapping
    public UserDto store(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> show(@PathVariable String username) {
        Optional<UserDto> optionalUser = userService.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalUser.orElseThrow());
    }
}
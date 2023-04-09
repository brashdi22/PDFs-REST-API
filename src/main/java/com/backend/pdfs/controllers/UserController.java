package com.backend.pdfs.controllers;

import com.backend.pdfs.entities.User;
import com.backend.pdfs.errorHandling.CustomException;
import com.backend.pdfs.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/user/register")
    public ResponseEntity<?> addUser(@RequestParam(value = "username") String username,
                                     @RequestParam(value = "password") String password) throws CustomException {
        if (userService.existsByUsername(username))
            throw new CustomException("Username already exists", HttpStatus.CONFLICT);

        userService.addUser(new User(username, passwordEncoder.encode(password)));
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password) throws CustomException {

        User user = userService.getUserByUsername(username)
                .orElseThrow(()-> new CustomException("User '"+ username + "' cannot be found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException("Incorrect password", HttpStatus.UNAUTHORIZED);


        userService.deleteByUsername(username);
        return ResponseEntity.ok("User deleted successfully");
    }

}
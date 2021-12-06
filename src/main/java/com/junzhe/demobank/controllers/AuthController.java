package com.junzhe.demobank.controllers;

import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.repository.UserRepository;
import com.junzhe.demobank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtUser> login(@Valid @RequestBody UserPayload payload,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        JwtUser jwtUser = userService.login(payload, request, response);
        return new ResponseEntity<>(jwtUser, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        if (userService.logout(response)) {
            return new ResponseEntity<>("logout successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Internal Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user")
    public ResponseEntity<JwtUser> createUser(@Valid @RequestBody UserPayload payload,
                                              HttpServletRequest request, HttpServletResponse response) {
        JwtUser jwtUser = userService.createUser(payload, request, response);
        return new ResponseEntity<>(jwtUser, HttpStatus.OK);
    }
}

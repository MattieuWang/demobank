package com.junzhe.demobank.controllers;

import com.junzhe.demobank.models.JwtUser;
import com.junzhe.demobank.models.UserPayload;
import com.junzhe.demobank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserPayload payload,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            JwtUser jwtUser = userRepository.login(payload, request, response);
            if (jwtUser == null) {
                return new ResponseEntity<>(new Error("Internal Error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(jwtUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        if (userRepository.logout(response)) {
            return new ResponseEntity<>("logout successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Internal Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user")
    public ResponseEntity<JwtUser> createUser(@RequestBody UserPayload payload,
                                              HttpServletRequest request, HttpServletResponse response) {
        try {
            JwtUser jwtUser = userRepository.createUser(payload, request, response);
            return new ResponseEntity<>(jwtUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "User creation failed", e
            );
        }
    }
}

package com.junzhe.demobank.controllers;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<UserInfo> getUserInfo() {
        try {
            UserInfo info = userRepository.getCurrentUserIfo();
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = {"/operations", "/operations/{num}"})
    public ResponseEntity<List<Operation>> getOperations (@PathVariable(name = "num", required = false) Integer num) {
        try {
            num = num == null || num < 1 ? 1 : num;
            List<Operation> ops = userRepository.getOperations(num);
            return new ResponseEntity<>(ops, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e
            );
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<Receipt> deposit(@RequestBody Map<String, Object>payload) {
        try {
            Receipt receipt = userRepository.deposit((double)payload.getOrDefault("amount", 0));
            return new ResponseEntity<Receipt>(receipt, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e
            );
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Receipt> withdraw(@RequestBody Map<String, Object>payload) {
        try {
            Receipt receipt = userRepository.withdraw((double)payload.getOrDefault("amount", 0));
            return new ResponseEntity<Receipt>(receipt, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e
            );
        }
    }

}

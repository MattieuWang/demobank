package com.junzhe.demobank.controllers;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<UserInfo> getUserInfo() {
        UserInfo info = userService.getCurrentUserInfo();
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping(value = {"/operations", "/operations/{num}"})
    public ResponseEntity<List<Operation>> getOperations (@PathVariable(name = "num", required = false) Integer num) {
        num = num == null || num < 1 ? 1 : num;
        List<Operation> ops = userService.getOperations(num);
        return new ResponseEntity<>(ops, HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Receipt> deposit(@RequestBody Map<String, Object>payload) {
        Receipt receipt = userService.deposit((double)payload.getOrDefault("amount", 0));
        return new ResponseEntity<>(receipt, HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Receipt> withdraw(@RequestBody Map<String, Object>payload) {
        Receipt receipt = userService.withdraw((double)payload.getOrDefault("amount", 0));
        return new ResponseEntity<Receipt>(receipt, HttpStatus.OK);
    }

}

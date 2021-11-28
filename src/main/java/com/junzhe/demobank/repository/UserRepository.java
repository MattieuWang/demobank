package com.junzhe.demobank.repository;

import com.junzhe.demobank.models.*;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.session.SessionManager;
import com.junzhe.demobank.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Repository
public class UserRepository {

    private Map<String, User> users;

    @Autowired
    SessionManager sessionManager;


    public UserRepository() {
        this.users = new HashMap<>();
    }

    public String getCurrentUserIfo() {
        JwtUser jwtUser = sessionManager.getCurrentUser();
        if (jwtUser == null) {
            return "";
        }
        User user = users.getOrDefault(jwtUser.getId(), null);
        if (user == null) {
            return "";
        }
        return user.toJson();
    }

    public JwtUser createUser(UserPayload payload, HttpServletRequest request, HttpServletResponse response) {
        if (payload.getUsername() == null || payload.getPassword() == null) {
            throw new IllegalArgumentException("Input error");
        }
        for (User user : users.values()) {
            if (user.getUsername().equals(payload.getUsername())) {
                throw new IllegalArgumentException("Input error");
            }
        }
        User user = new User(payload.getUsername(), StringUtil.encryptPW(payload.getPassword()));
        users.put(user.getId(), user);
        JwtUser jwtUser = new JwtUser(user.getId(), user.getUsername());
        sessionManager.setSessionUser(jwtUser, request, response);
        return jwtUser;
    }

    public JwtUser login(UserPayload payload, HttpServletRequest request, HttpServletResponse response) {
        if (payload.getUsername() == null || payload.getPassword() == null) {
            throw new IllegalArgumentException("Input error");
        }
        for (User u : users.values()) {
            if (u.getUsername().equals(payload.getUsername())
                    && StringUtil.isPwCorrect(payload.getPassword(), u.getPassword())) {
                JwtUser jwtUser = new JwtUser(u.getId(), u.getUsername());
                sessionManager.setSessionUser(jwtUser, request, response);
                return jwtUser;
            }
        }
        throw new IllegalArgumentException("username or password error");
    }

    public boolean logout(HttpServletResponse response) {
        return sessionManager.logout(response);
    }

    public Receipt deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Input amount error");
        }
        JwtUser jwtUser = sessionManager.getCurrentUser();
        User user = users.getOrDefault(jwtUser.getId(), null);
        if (user == null) {
            throw new NoSuchElementException("No user found");
        }
        user.deposit(amount);
        Receipt receipt = new Receipt(jwtUser.getId(), jwtUser.getUsername(), "Success", amount, user.getBalance(), OperationName.DEPOSIT);
        sessionManager.getSession().setReceipt(receipt);
        return receipt;
    }

    public Receipt withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Input amount error");
        }
        JwtUser jwtUser = sessionManager.getCurrentUser();
        User user = users.getOrDefault(jwtUser.getId(), null);
        if (user == null) {
            throw new NoSuchElementException("No user found");
        }
        if (user.getBalance() < amount) {
            throw new IllegalArgumentException("Input amount error");
        }
        user.withdraw(amount);
        Receipt receipt = new Receipt(jwtUser.getId(), jwtUser.getUsername(), "Success", amount, user.getBalance(), OperationName.WITHDRAW);
        sessionManager.getSession().setReceipt(receipt);
        return receipt;
    }

    public String test() {
        return "test";
    }

    public List<Operation> getOperations(int num) {
        return sessionManager.getOperations(num);
    }

}

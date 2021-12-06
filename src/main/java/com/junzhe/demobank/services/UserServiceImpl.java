package com.junzhe.demobank.services;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.User;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.repository.UserRepository;
import com.junzhe.demobank.session.SessionManager;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    SessionManager sessionManager;
    @Autowired
    UserRepository repository;

    public UserInfo getCurrentUserInfo() {
        try {
            User user = repository.getCurrentUserInfo(sessionManager.getCurrentUser());
            return new UserInfo(user.getId(), user.getUsername(), user.getBalance());
        } catch (Exception e) {
            throw new InternalException("User not found");
        }
    }

    public JwtUser createUser(UserPayload payload, HttpServletRequest request, HttpServletResponse response) {
        if (payload.getUsername() == null || payload.getPassword() == null) {
            throw new IllegalArgumentException("Input error");
        }
        User user = repository.createUser(payload);
        JwtUser jwtUser = new JwtUser(user.getId(), user.getUsername());
        sessionManager.setSessionUser(jwtUser, request, response);
        return jwtUser;
    }

    public JwtUser login(UserPayload payload, HttpServletRequest request, HttpServletResponse response) {
        if (payload.getUsername() == null || payload.getPassword() == null) {
            throw new IllegalArgumentException("Input error");
        }
        User user = repository.login(payload);
        JwtUser jwtUser = new JwtUser(user.getId(), user.getUsername());
        sessionManager.setSessionUser(jwtUser, request, response);
        return jwtUser;
    }

    public boolean logout(HttpServletResponse response) {
        return sessionManager.logout(response);
    }

    public Receipt deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Input amount error");
        }
        JwtUser jwtUser = sessionManager.getCurrentUser();
        User user = repository.update(amount, jwtUser);
        Receipt receipt = new Receipt(jwtUser.getId(), jwtUser.getUsername(), "Success", amount, user.getBalance(), OperationName.DEPOSIT);
        sessionManager.getSession().setReceipt(receipt);
        return receipt;
    }

    public Receipt withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Input amount error");
        }
        JwtUser jwtUser = sessionManager.getCurrentUser();
        User user = repository.update(amount*-1, jwtUser);
        Receipt receipt = new Receipt(jwtUser.getId(), jwtUser.getUsername(), "Success", amount, user.getBalance(), OperationName.WITHDRAW);
        sessionManager.getSession().setReceipt(receipt);
        return receipt;
    }

    public List<Operation> getOperations (int number) {
        return sessionManager.getOperations(number);
    }

}




































package com.junzhe.demobank.services;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.models.user.UserPayload;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public interface UserService {
    UserInfo getCurrentUserInfo();
    JwtUser createUser(UserPayload payload, HttpServletRequest request, HttpServletResponse response);
    JwtUser login(UserPayload payload, HttpServletRequest request, HttpServletResponse response);
    boolean logout(HttpServletResponse response);
    Receipt deposit(double amount);
    Receipt withdraw(double amount);
    List<Operation> getOperations (int number);
}

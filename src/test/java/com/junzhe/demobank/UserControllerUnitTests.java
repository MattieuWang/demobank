package com.junzhe.demobank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.controllers.UserController;
import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.Session;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.services.UserService;
import com.junzhe.demobank.services.UserServiceImpl;
import com.junzhe.demobank.session.SessionFilter;
import com.junzhe.demobank.session.SessionManager;
import com.junzhe.demobank.utils.CookieUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerUnitTests {

    @MockBean
    UserService userService;

    @MockBean
    SessionManager manager;

    @Autowired
    MockMvc mvc;

    @Test
    public void getUserInfoTest_thenStatus200() throws Exception {
        JwtUser jwtUser = new JwtUser("id", "test");
        UserInfo info = new UserInfo("id", "test", 0);
        String jwt = CookieUtils.generateJWT(jwtUser);
        Cookie cookie = new Cookie("session_id", jwt);
        when(userService.getCurrentUserInfo()).thenReturn(info);
        when(manager.getCurrentUser()).thenReturn(jwtUser);

        mvc.perform(get("/user")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    public void getUserInfoTest_thenStatus401() throws Exception {
        JwtUser jwtUser = new JwtUser("id", "test");
        UserInfo info = new UserInfo("id", "test", 0);
        String jwt = CookieUtils.generateJWT(jwtUser);
        Cookie cookie = new Cookie("session_id", jwt);
        when(userService.getCurrentUserInfo()).thenReturn(info);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        mvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOperationsTest_thenStatus200() throws Exception {
        JwtUser jwtUser = new JwtUser("id", "test");
        UserInfo info = new UserInfo("id", "test", 0);
        String jwt = CookieUtils.generateJWT(jwtUser);
        Cookie cookie = new Cookie("session_id", jwt);
        List<Operation> ops = new ArrayList<>();
        Operation op1 = new Operation(); Operation op2 = new Operation();
        Operation op3 = new Operation();
        ops.add(op1); ops.add(op2); ops.add(op3);
        when(userService.getCurrentUserInfo()).thenReturn(info);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(userService.getOperations(1)).thenReturn(ops);
        mvc.perform(get("/user/operations/1")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(ops.size())));
    }

    @Test
    public void testDeposit_thenStatus200() throws Exception {
        JwtUser jwtUser = new JwtUser("id", "test");
        UserInfo info = new UserInfo("id", "test", 0);
        String jwt = CookieUtils.generateJWT(jwtUser);
        Cookie cookie = new Cookie("session_id", jwt);
        Session session = new Session(); session.setCurrent(jwtUser);
        double amount = 300;
        Receipt receipt = new Receipt("id", "test", "Success", amount, amount,OperationName.DEPOSIT);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(payload);
        when(userService.getCurrentUserInfo()).thenReturn(info);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(userService.deposit(amount)).thenReturn(receipt);
        when(manager.getSession()).thenReturn(session);
        mvc.perform(post("/user/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.balance").value(amount))
                .andExpect(jsonPath("$.msg").value("Success"))
                .andExpect(jsonPath("$.op").value(OperationName.DEPOSIT.toString()));
    }

    @Test
    public void testWithdraw_thenStatus200() throws Exception {
        double amount = 300;
        JwtUser jwtUser = new JwtUser("id", "test");
        UserInfo info = new UserInfo("id", "test", amount*2);
        String jwt = CookieUtils.generateJWT(jwtUser);
        Cookie cookie = new Cookie("session_id", jwt);
        Session session = new Session(); session.setCurrent(jwtUser);
        Receipt receipt = new Receipt("id", "test", "Success", amount, amount,OperationName.WITHDRAW);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(payload);
        when(userService.getCurrentUserInfo()).thenReturn(info);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(userService.withdraw(amount)).thenReturn(receipt);
        when(manager.getSession()).thenReturn(session);
        mvc.perform(post("/user/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.balance").value(amount))
                .andExpect(jsonPath("$.msg").value("Success"))
                .andExpect(jsonPath("$.op").value(OperationName.WITHDRAW.toString()));
    }


}
























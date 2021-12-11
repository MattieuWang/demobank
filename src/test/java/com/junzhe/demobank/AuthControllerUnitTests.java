package com.junzhe.demobank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.controllers.AuthController;
import com.junzhe.demobank.controllers.UserController;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.services.UserService;
import com.junzhe.demobank.services.UserServiceImpl;
import com.junzhe.demobank.session.SessionManager;
import com.junzhe.demobank.utils.CookieUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerUnitTests {

    @MockBean
    UserServiceImpl userService;

    @MockBean
    SessionManager manager;

    @Autowired
    MockMvc mvc;

    @Test
    public void loginTest_thenStatus200() throws Exception {
        String username = "test";
        String password = "test111";
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername(username);
        userPayload.setPassword(password);
        JwtUser jwtUser = new JwtUser("id", username);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userPayload);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(userService.login(userPayload, request, response)).thenReturn(jwtUser);
        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void CreateUserTest_thenStatus200() throws Exception {
        String username = "test";
        String password = "test111";
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername(username);
        userPayload.setPassword(password);
        JwtUser jwtUser = new JwtUser("id", username);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userPayload);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(userService.login(userPayload, request, response)).thenReturn(jwtUser);
        mvc.perform(post("/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

}































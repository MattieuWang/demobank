package com.junzhe.demobank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.utils.CookieUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTests {

    final static private String TOKEN = "session_id";

    @Autowired
    private MockMvc mvc;

    @Test
    private Cookie createTestSession(String username, String password) throws Exception {
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername(username);
        userPayload.setPassword(password);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userPayload);

        MvcResult mvcResult = mvc.perform(post("/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(cookie().exists(TOKEN))
                .andReturn();
        return mvcResult.getResponse().getCookie(TOKEN);
    }

    @Test
    public void loginWithValidParams_thenStatus200() throws Exception {
        String username = "test1";
        String password = "test111";
        Cookie cookie = createTestSession(username, password);
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername(username);
        userPayload.setPassword(password);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userPayload);
        mvc.perform(post("/auth/login")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    public void loginWithInValidParams_thenStatus500() throws Exception {
        String username = "test2";
        String password = "test111";
        Cookie cookie = createTestSession(username, password);
        UserPayload userPayload = new UserPayload();
        userPayload.setUsername(username);
        userPayload.setPassword("password");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userPayload);
        mvc.perform(post("/auth/login")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("username or password error"));
    }

    @Test
    public void logoutWithSession_thenStatus200() throws Exception {
        String username = "test3";
        String password = "test111";
        Cookie cookie = createTestSession(username, password);
        mvc.perform(post("/auth/logout")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string("logout successfully"));
    }


}

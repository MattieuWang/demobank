package com.junzhe.demobank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.models.user.UserPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTests {

	final static private String TOKEN = "session_id";

	@Autowired
	private MockMvc mvc;

	@Test
	public void checkUserInfoWithoutSession_thenStatus401()
		throws Exception {
		mvc.perform(get("/user")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("You're not authorized"));
	}

	@Test
	public void checkUserInfoWithSession_thenStatus200() throws Exception {
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);

		mvc.perform(get("/user")
						.cookie(cookie))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.username").value(username))
				.andExpect(jsonPath("$.balance").value(0));
	}

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
	public void depositWithValidAmount_thenStatus200 () throws Exception{
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Map<String, Double> payload = new HashMap<>();
		double amount = 300;
		payload.put("amount", amount);
		String requestJson = ow.writeValueAsString(payload);
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
	public void depositWithInvalidAmount_thenStatus500 () throws Exception{
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Map<String, Double> payload = new HashMap<>();
		double amount = -300;
		payload.put("amount", amount);
		String requestJson = ow.writeValueAsString(payload);
		mvc.perform(post("/user/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
						.cookie(cookie))
				.andExpect(status().isInternalServerError())
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Input amount error")));
	}

	@Test
	public void withdrawWithValidAmount_thenStatus200 () throws Exception{
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Map<String, Double> payload = new HashMap<>();
		double amount = 300;
		payload.put("amount", amount);
		String requestJson = ow.writeValueAsString(payload);
		mvc.perform(post("/user/deposit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
						.cookie(cookie));
		mvc.perform(post("/user/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
						.cookie(cookie))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.amount").value(amount))
				.andExpect(jsonPath("$.balance").value(0))
				.andExpect(jsonPath("$.msg").value("Success"))
				.andExpect(jsonPath("$.op").value(OperationName.WITHDRAW.toString()));
	}

	@Test
	public void withdrawWithInvalidAmount_thenStatus500 () throws Exception{
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Map<String, Double> payload = new HashMap<>();
		double amount = 300;
		payload.put("amount", amount);
		String requestJson = ow.writeValueAsString(payload);
		mvc.perform(post("/user/withdraw")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
						.cookie(cookie))
				.andExpect(status().isInternalServerError())
				.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Input amount error")));
	}


	@Test
	public void checkOptions_thenStatus200 () throws Exception {
		String username = "test";
		String password = "test111";
		Cookie cookie = createTestSession(username, password);
		mvc.perform(get("/user/operations")
						.cookie(cookie))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].operationName").value(OperationName.LOG_IN.toString()));
	}

}







































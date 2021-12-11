package com.junzhe.demobank;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.models.user.UserPayload;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemTests {

    @Test
    public void testCreateDepositWithdrawOperation() {
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:8080/";

        UserPayload payload = new UserPayload(); payload.setPassword("aaa"); payload.setUsername("user4q5");
        HttpEntity<UserPayload> request = new HttpEntity<>(payload);
        ResponseEntity<UserInfo> response = template.exchange(url+"auth/user", HttpMethod.POST,request, UserInfo.class);
        assertEquals(1, response.getHeaders().get("Set-Cookie").size());
        String cookie = response.getHeaders().get("Set-Cookie").get(0);

        Map<String, Double> amount = new HashMap<>();
        amount.put("amount", 300.0);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        HttpEntity<Map<String, Double>> amountPayload = new HttpEntity<>(amount, headers);
        ResponseEntity<Receipt> depositReceipt = template.postForEntity(url+"/user/deposit", amountPayload, Receipt.class);
        assertThat(depositReceipt.getBody()).extracting(Receipt::getMsg).isEqualTo("Success");
        assertThat(depositReceipt.getBody()).extracting(Receipt::getAmount).isEqualTo(300.0);
        assertThat(depositReceipt.getBody()).extracting(Receipt::getBalance).isEqualTo(300.0);

        ResponseEntity<Receipt> withdrawReceipt = template.postForEntity(url+"user/withdraw", amountPayload, Receipt.class);
        assertThat(withdrawReceipt.getBody()).extracting(Receipt::getMsg).isEqualTo("Success");
        assertThat(withdrawReceipt.getBody()).extracting(Receipt::getAmount).isEqualTo(300.0);
        assertThat(withdrawReceipt.getBody()).extracting(Receipt::getBalance).isEqualTo(0.0);

        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<Operation[]> operationResponse = template.exchange(url+"user/operations", HttpMethod.GET, entity, Operation[].class);
        assertEquals(3, operationResponse.getBody().length);
    }


}



















package com.junzhe.demobank.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class User {

    private String id;
    private String username;
    private String password;
    private double balance;
    private List<String> operation_ids;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0f;
        this.id = StringUtil.getUUID();
        this.operation_ids = new ArrayList<>();
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public void addOperation(String id) {
        operation_ids.add(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }

    public String toJson() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id", this.id);
        jsonMap.put("username", this.username);
        jsonMap.put("balance", this.balance);
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}

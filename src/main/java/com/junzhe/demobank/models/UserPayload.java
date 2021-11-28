package com.junzhe.demobank.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPayload {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "UserPayload{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

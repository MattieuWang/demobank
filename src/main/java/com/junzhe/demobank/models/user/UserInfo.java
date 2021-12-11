package com.junzhe.demobank.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private String id;
    private String username;
    private double balance;

    public UserInfo(String id, String username, double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}

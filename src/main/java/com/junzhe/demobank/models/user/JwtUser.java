package com.junzhe.demobank.models.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtUser {
    private String id;
    private String username;
    public JwtUser(String id, String username) {
        this.id = id;
        this.username = username;
    }
}

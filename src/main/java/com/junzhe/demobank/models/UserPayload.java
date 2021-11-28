package com.junzhe.demobank.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserPayload {
    @NotBlank
    @Size(min=3, max=20)
    private String username;
    @NotBlank
    @Size(min=3, max = 20)
    private String password;

    @Override
    public String toString() {
        return "UserPayload{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

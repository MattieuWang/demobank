package com.junzhe.demobank.models;

import com.junzhe.demobank.models.user.JwtUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Session {
    private JwtUser current;

    private Receipt receipt;

    public Session() {
//        this.operations = new ArrayList<>();
//        this.user_op = new HashMap<>();
    }


}

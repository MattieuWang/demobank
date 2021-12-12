package com.junzhe.demobank.models;

import com.junzhe.demobank.models.user.JwtUser;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Semaphore;

@Getter
@Setter
public class Session {
    private JwtUser current;

    private Receipt receipt;

    private Semaphore semaphore;

    public Session() {
        semaphore = new Semaphore(1);
//        this.operations = new ArrayList<>();
//        this.user_op = new HashMap<>();
    }


}

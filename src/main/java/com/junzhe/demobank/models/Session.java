package com.junzhe.demobank.models;

import com.junzhe.demobank.models.operations.Operation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

package com.junzhe.demobank.models;

import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Receipt{

    private String user_id;
    private String username;
    private double amount;
    private double balance;
    private String msg;
    private OperationName op;

    public Receipt(String user_id, String username,String msg, double amount, double balance, OperationName op) {
        this.amount = amount;
        this.user_id = user_id;
        this.msg = msg;
        this.balance = balance;
        this.op = op;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", msg='" + msg + '\'' +
                ", op=" + op +
                '}';
    }
}
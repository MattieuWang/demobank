package com.junzhe.demobank.models.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Operation {

    private String id;
    private Date timestamp;
    private OperationName operationName;
    //private Result result;
    private String user_id;
    private Receipt receipt;

    public Operation() {
        this.id = StringUtil.getUUID();
        this.timestamp = new Date();
    }

    public Operation(
            OperationName name,
            //Result result,
            String user_id
    ) {
        this.id = StringUtil.getUUID();
        this.timestamp = new Date();
        this.operationName = name;
        //this.result = result;
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "timestamp=" + timestamp +
                ", operationName=" + operationName +
                //", result=" + result +
                ", user_id='" + user_id + '\'' +
                (receipt != null ? (", receipt='" + receipt.toString() + '\'') : "") +
                '}';
    }

    public String toJson() {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}

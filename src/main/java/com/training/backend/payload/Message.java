package com.training.backend.payload;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {
    private String code;
    private List<String> params;

    public Message() {
        this.params = new ArrayList<>();
    }

    public Message(String code, List<String> params) {
        this.code = code;
        this.params = params;
    }
}

package com.training.backend.payload.response;

import com.training.backend.payload.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private Message message;

    public ErrorResponse(String code) {
        this.code = code;
    }

    public void addMessage(String code, List<String> params) {
        Message message = new Message();
        message.setCode(code);
        message.setParams(params);
        this.message = message;
    }


}

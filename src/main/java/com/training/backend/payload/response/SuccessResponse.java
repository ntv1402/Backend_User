package com.training.backend.payload.response;

import com.training.backend.payload.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SuccessResponse {
    private String code;
    private long userId;
    private Message message;

    public SuccessResponse(String code, long userId) {
        this.code = code;
        this.userId = userId;
    }

    public void addMessage(String code, List<String> params) {
        Message message = new Message();
        message.setCode(code);
        message.setParams(params);
        this.message = message;
    }
}

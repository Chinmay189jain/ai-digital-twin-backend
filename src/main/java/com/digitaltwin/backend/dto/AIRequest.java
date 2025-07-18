package com.digitaltwin.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {

    private String model;
    private List<Message> messages;
    private double temperature;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role; // "user", "system", or "assistant"
        private String content;
    }
}

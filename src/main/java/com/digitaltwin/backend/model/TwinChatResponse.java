package com.digitaltwin.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TwinChatResponse {
    private String question;
    private String aiResponse;
    private LocalDateTime timestamp;
}

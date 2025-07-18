package com.digitaltwin.backend.dto;

import lombok.Data;

@Data
public class TwinQuestionRequest {
    private String twinIdentity;
    private String userQuestion;
}

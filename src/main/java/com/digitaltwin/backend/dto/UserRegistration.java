package com.digitaltwin.backend.dto;

import lombok.Data;

@Data
public class UserRegistration {

    private String name;
    private String email;
    private String password;
}
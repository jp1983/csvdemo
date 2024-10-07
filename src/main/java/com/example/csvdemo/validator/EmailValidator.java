package com.example.csvdemo.validator;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    public static boolean validate(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
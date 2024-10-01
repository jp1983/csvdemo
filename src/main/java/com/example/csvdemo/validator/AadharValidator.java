package com.example.csvdemo.validator;

import org.springframework.stereotype.Component;

@Component
public class AadharValidator {
    public boolean validate(String aadhar) {
        return aadhar != null && aadhar.matches("\\d{12}");
    }
}

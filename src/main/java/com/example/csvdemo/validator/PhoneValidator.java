package com.example.csvdemo.validator;

import org.springframework.stereotype.Component;

@Component
public class PhoneValidator {
    public boolean validate(String phone) {
        return phone == null || phone.length() == 10;//phone.matches("\\d{10}");
    }
}
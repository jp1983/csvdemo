package com.example.csvdemo.validator;

import org.springframework.stereotype.Component;

@Component
public class MandatoryFieldValidator {
    public boolean validate(String employeeId, String email, String firstName, String lastName, String aadhar) {
        return  (employeeId != null && email != null && firstName != null && lastName != null && aadhar != null);
    }
}
package com.example.csvdemo.controller;

import com.example.csvdemo.model.Employee;
import com.example.csvdemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/search")
    public ResponseEntity<?> searchEmployeeByName(@RequestParam("name") String name) {
        if (name != null && !name.isEmpty()) {
            List<Employee> employeeList = employeeService.searchEmployeeByName(name);
            return ResponseEntity.ok(employeeList);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search query can not be empty");
        }

    }

}

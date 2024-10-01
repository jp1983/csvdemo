package com.example.csvdemo.service;

import com.example.csvdemo.model.Employee;
import com.example.csvdemo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> searchEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

}

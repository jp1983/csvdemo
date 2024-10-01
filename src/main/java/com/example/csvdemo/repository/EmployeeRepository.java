package com.example.csvdemo.repository;

import com.example.csvdemo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select emp from Employee emp where LOWER(emp.firstName) LIKE %:name% or LOWER(emp.lastName) LIKE %:name%")
    List<Employee> findByName(String name);

    List<Employee> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}
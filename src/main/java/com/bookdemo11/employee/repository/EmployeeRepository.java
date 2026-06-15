package com.bookdemo11.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmployeeMail(String employeeMail);
    boolean existsByEmployeeMail(String employeeMail);
    boolean existsByEmployeeMailAndEmployeeIdNot(String employeeMail, Integer employeeId);
}
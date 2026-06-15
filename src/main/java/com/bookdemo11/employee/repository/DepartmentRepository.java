package com.bookdemo11.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.employee.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByStatus(Byte status);
}
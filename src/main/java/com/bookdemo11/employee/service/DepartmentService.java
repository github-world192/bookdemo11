package com.bookdemo11.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.employee.entity.Department;
import com.bookdemo11.employee.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public List<Department> findActive() {
        return departmentRepository.findByStatus((byte) 1);
    }

    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }

    @Transactional
    public Department save(String name, Byte status) {
        Department dept = new Department();
        dept.setDepartmentName(name);
        dept.setStatus(status != null ? status : (byte) 1);
        return departmentRepository.save(dept);
    }

    @Transactional
    public Department update(Integer id, String name, Byte status) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("部門不存在"));
        dept.setDepartmentName(name);
        dept.setStatus(status != null ? status : (byte) 1);
        return departmentRepository.save(dept);
    }
}
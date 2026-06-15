package com.bookdemo11.employee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.employee.dto.EmployeeForm;
import com.bookdemo11.employee.entity.Department;
import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.entity.JobTitle;
import com.bookdemo11.employee.repository.DepartmentRepository;
import com.bookdemo11.employee.repository.EmployeeRepository;
import com.bookdemo11.employee.repository.JobTitleRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleRepository jobTitleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           JobTitleRepository jobTitleRepository,
                           PasswordEncoder passwordEncoder,
                           PermissionService permissionService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(Integer id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> findByMail(String mail) {
        return employeeRepository.findByEmployeeMail(mail);
    }

    @Transactional
    public Employee create(EmployeeForm form) {
        if (employeeRepository.existsByEmployeeMail(form.getEmployeeMail())) {
            throw new IllegalArgumentException("此員工信箱已存在");
        }
        if (form.getEmployeePassword() == null || form.getEmployeePassword().isBlank()) {
            throw new IllegalArgumentException("請設定登入密碼");
        }

        Employee employee = mapFormToEntity(new Employee(), form);
        employee.setEmployeePassword(passwordEncoder.encode(form.getEmployeePassword()));
        employee.setHireDate(form.getHireDate() != null ? form.getHireDate() : LocalDate.now());
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Integer id, EmployeeForm form) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));

        if (employeeRepository.existsByEmployeeMailAndEmployeeIdNot(form.getEmployeeMail(), id)) {
            throw new IllegalArgumentException("此員工信箱已被使用");
        }

        mapFormToEntity(employee, form);
        if (form.getEmployeePassword() != null && !form.getEmployeePassword().isBlank()) {
            employee.setEmployeePassword(passwordEncoder.encode(form.getEmployeePassword()));
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public void toggleStatus(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));
        if (permissionService.isSystemAdmin(employee)) {
            throw new IllegalArgumentException("無法停權系統管理員");
        }
        employee.setStatus(employee.getStatus() == 1 ? (byte) 0 : (byte) 1);
        employeeRepository.save(employee);
    }

    @Transactional
    public void delete(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));
        if (permissionService.isSystemAdmin(employee)) {
            throw new IllegalArgumentException("無法刪除系統管理員");
        }
        employeeRepository.deleteById(id);
    }

    private Employee mapFormToEntity(Employee employee, EmployeeForm form) {
        Department department = departmentRepository.findById(form.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("部門不存在"));
        JobTitle jobTitle = jobTitleRepository.findById(form.getJobTitleId())
                .orElseThrow(() -> new IllegalArgumentException("職稱不存在"));

        employee.setEmployeeName(form.getEmployeeName());
        employee.setEmployeeMail(form.getEmployeeMail());
        employee.setPhone(form.getPhone());
        employee.setAddress(form.getAddress());
        employee.setDepartment(department);
        employee.setJobTitle(jobTitle);
        employee.setGender(form.getGender());
        employee.setHireDate(form.getHireDate());
        employee.setStatus(form.getStatus() != null ? form.getStatus() : (byte) 1);
        return employee;
    }
}
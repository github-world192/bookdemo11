package com.bookdemo11.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.repository.EmployeeRepository;
import com.bookdemo11.employee.service.PermissionService;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final PermissionService permissionService;

    public AdminUserDetailsService(EmployeeRepository employeeRepository,
                                   PermissionService permissionService) {
        this.employeeRepository = employeeRepository;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmployeeMail(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到員工帳號"));

        if (employee.getStatus() != null && employee.getStatus() == 0) {
            throw new UsernameNotFoundException("此帳號已離職或停用");
        }

        if (employee.getDepartment() == null || employee.getDepartment().getStatus() == 0) {
            throw new UsernameNotFoundException("所屬部門已停用");
        }

        if (employee.getJobTitle() == null || employee.getJobTitle().getStatus() == 0) {
            throw new UsernameNotFoundException("職稱已停用");
        }

        return new AdminPrincipal(employee, permissionService.resolveAuthorities(employee));
    }
}
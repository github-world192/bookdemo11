package com.bookdemo11.config;

import java.util.Set;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.bookdemo11.employee.entity.Employee;

@ControllerAdvice("com.bookdemo11.employee.controller")
public class AdminModelAdvice {

    private final AdminSecurity adminSecurity;

    public AdminModelAdvice(AdminSecurity adminSecurity) {
        this.adminSecurity = adminSecurity;
    }

    @ModelAttribute("currentEmployee")
    public Employee currentEmployee() {
        return adminSecurity.currentEmployee().orElse(null);
    }

    @ModelAttribute("adminPrincipal")
    public AdminPrincipal adminPrincipal() {
        return adminSecurity.currentPrincipal().orElse(null);
    }

    @ModelAttribute("isSuperAdmin")
    public boolean isSuperAdmin() {
        return adminSecurity.isSystemAdmin();
    }

    @ModelAttribute("adminPermissions")
    public Set<String> adminPermissions() {
        return adminSecurity.currentPermissionCodes();
    }
}
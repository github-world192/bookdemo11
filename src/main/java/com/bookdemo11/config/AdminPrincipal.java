package com.bookdemo11.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bookdemo11.employee.entity.Employee;

/**
 * 員工登入後的身份物件，存放於 HttpSession 的 SecurityContext 中（Session-based RBAC）。
 */
public class AdminPrincipal implements UserDetails {

    private final Integer employeeId;
    private final String employeeMail;
    private final String employeeName;
    private final String departmentName;
    private final String jobTitleName;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public AdminPrincipal(Employee employee, Collection<? extends GrantedAuthority> authorities) {
        this.employeeId = employee.getEmployeeId();
        this.employeeMail = employee.getEmployeeMail();
        this.employeeName = employee.getEmployeeName();
        this.departmentName = employee.getDepartment() != null
                ? employee.getDepartment().getDepartmentName() : "";
        this.jobTitleName = employee.getJobTitle() != null
                ? employee.getJobTitle().getJobTitleName() : "";
        this.password = employee.getEmployeePassword();
        this.authorities = authorities;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return employeeMail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
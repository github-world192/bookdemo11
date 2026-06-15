package com.bookdemo11.config;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.entity.PermissionCode;
import com.bookdemo11.employee.repository.EmployeeRepository;

/**
 * Session 內權限檢查工具，供 @PreAuthorize 與 Controller 使用。
 */
@Component("adminSecurity")
public class AdminSecurity {

    private final EmployeeRepository employeeRepository;

    public AdminSecurity(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean hasPermission(String authority) {
        return currentAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a) || authority.equals(a));
    }

    public boolean hasPermission(PermissionCode code) {
        return hasPermission(code.name());
    }

    public boolean isSystemAdmin() {
        return currentAuthorities().contains("ROLE_ADMIN");
    }

    public Set<String> currentPermissionCodes() {
        return currentAuthorities().stream()
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    public Optional<AdminPrincipal> currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof AdminPrincipal adminPrincipal) {
            return Optional.of(adminPrincipal);
        }
        return Optional.empty();
    }

    public Optional<Employee> currentEmployee() {
        return currentPrincipal()
                .flatMap(p -> employeeRepository.findById(p.getEmployeeId()));
    }

    private Set<String> currentAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Set.of();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
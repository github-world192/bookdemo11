package com.bookdemo11.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.employee.repository.EmployeeRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeRepository employeeRepository;

    private static final List<String> LANDING_PAGES = List.of(
            "/admin/dashboard",
            "/admin/orders",
            "/admin/employees",
            "/admin/org",
            "/admin/room-types",
            "/admin/rooms",
            "/admin/members");

    public AdminAuthenticationSuccessHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        Integer employeeId = null;
        if (authentication.getPrincipal() instanceof AdminPrincipal principal) {
            employeeId = principal.getEmployeeId();
        }
        if (employeeId != null) {
            employeeRepository.findById(employeeId).ifPresent(employee -> {
                employee.setLastLoginTime(LocalDateTime.now());
                employeeRepository.save(employee);
            });
        }
        String target = resolveLandingPage(authentication);
        response.sendRedirect(request.getContextPath() + target);
    }

    private String resolveLandingPage(Authentication authentication) {
        if (hasAuthority(authentication, "ROLE_ADMIN")) {
            return "/admin/dashboard";
        }
        for (String page : LANDING_PAGES) {
            String required = pageToAuthority(page);
            if (required == null || hasAuthority(authentication, required)) {
                return page;
            }
        }
        return "/admin/access-denied";
    }

    private String pageToAuthority(String page) {
        return switch (page) {
            case "/admin/dashboard" -> "DASHBOARD_VIEW";
            case "/admin/employees" -> "EMPLOYEE_MANAGE";
            case "/admin/org" -> "ROLE_MANAGE";
            case "/admin/members" -> "MEMBER_MANAGE";
            case "/admin/room-types" -> "ROOM_TYPE_MANAGE";
            case "/admin/rooms" -> "ROOM_MANAGE";
            case "/admin/orders" -> "ORDER_MANAGE";
            default -> null;
        };
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(authority));
    }
}
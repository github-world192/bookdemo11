package com.bookdemo11.employee.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.entity.PermissionCode;

/**
 * 依職稱與部門解析後台功能權限，登入時寫入 Session 的 SecurityContext。
 * <ul>
 *   <li>職稱「系統管理員」→ 全部權限（ROLE_ADMIN）</li>
 *   <li>櫃檯部 → 儀表板、會員、訂房</li>
 *   <li>房務部 → 儀表板、房型、房間、訂房</li>
 *   <li>行銷部 → 儀表板、會員</li>
 * </ul>
 */
@Service
public class PermissionService {

    private static final String ADMIN_TITLE = "系統管理員";

    public List<GrantedAuthority> resolveAuthorities(Employee employee) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));

        if (employee.getJobTitle() != null && ADMIN_TITLE.equals(employee.getJobTitle().getJobTitleName())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            for (PermissionCode code : PermissionCode.values()) {
                authorities.add(new SimpleGrantedAuthority(code.name()));
            }
            return authorities;
        }

        String deptName = employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : "";
        Set<String> permissions = switch (deptName) {
            case "櫃檯部" -> Set.of(
                    PermissionCode.DASHBOARD_VIEW.name(),
                    PermissionCode.MEMBER_MANAGE.name(),
                    PermissionCode.ORDER_MANAGE.name());
            case "房務部" -> Set.of(
                    PermissionCode.DASHBOARD_VIEW.name(),
                    PermissionCode.ROOM_TYPE_MANAGE.name(),
                    PermissionCode.ROOM_MANAGE.name(),
                    PermissionCode.ORDER_MANAGE.name());
            case "行銷部" -> Set.of(
                    PermissionCode.DASHBOARD_VIEW.name(),
                    PermissionCode.MEMBER_MANAGE.name());
            default -> Set.of(PermissionCode.DASHBOARD_VIEW.name());
        };

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    public boolean hasPermission(Collection<? extends GrantedAuthority> authorities, PermissionCode code) {
        return authorities.stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals(code.name()));
    }

    public boolean isSystemAdmin(Employee employee) {
        return employee.getJobTitle() != null
                && ADMIN_TITLE.equals(employee.getJobTitle().getJobTitleName());
    }
}
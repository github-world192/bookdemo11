package com.bookdemo11.employee.entity;

public enum PermissionCode {
    DASHBOARD_VIEW("儀表板"),
    EMPLOYEE_MANAGE("員工管理"),
    ROLE_MANAGE("組織設定"),
    MEMBER_MANAGE("會員管理"),
    ROOM_TYPE_MANAGE("房型管理"),
    ROOM_MANAGE("房間管理"),
    ORDER_MANAGE("訂房管理");

    private final String label;

    PermissionCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
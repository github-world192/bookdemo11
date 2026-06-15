package com.bookdemo11.employee.dto;

import java.time.LocalDate;

import com.bookdemo11.employee.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeForm {

    private Integer employeeId;

    @NotBlank(message = "姓名不可為空")
    private String employeeName;

    @NotBlank(message = "信箱不可為空")
    private String employeeMail;

    private String employeePassword;

    @NotBlank(message = "電話不可為空")
    private String phone;

    private String address;

    @NotNull(message = "部門不可為空")
    private Integer departmentId;

    @NotNull(message = "職稱不可為空")
    private Integer jobTitleId;

    @NotNull(message = "性別不可為空")
    private Gender gender = Gender.OTHER;

    private LocalDate hireDate;

    private Byte status = 1;
}
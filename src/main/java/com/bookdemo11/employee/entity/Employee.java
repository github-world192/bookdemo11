package com.bookdemo11.employee.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = false)
    private Department department;

    @NotBlank
    @Column(name = "EMPLOYEE_NAME", nullable = false, length = 50)
    private String employeeName;

    @NotBlank
    @Column(name = "EMPLOYEE_MAIL", nullable = false, unique = true, length = 100)
    private String employeeMail;

    @NotBlank
    @Column(name = "EMPLOYEE_PASSWORD", nullable = false, length = 255)
    private String employeePassword;

    @NotBlank
    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_TITLE_ID", nullable = false)
    private JobTitle jobTitle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", nullable = false, columnDefinition = "VARCHAR(10)")
    private Gender gender = Gender.OTHER;

    @Column(name = "STATUS", nullable = false)
    private Byte status = 1;

    @NotNull
    @Column(name = "HIRE_DATE", nullable = false)
    private LocalDate hireDate;

    @Column(name = "LAST_LOGIN_TIME")
    private LocalDateTime lastLoginTime;
}
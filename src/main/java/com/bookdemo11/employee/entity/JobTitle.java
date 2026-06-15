package com.bookdemo11.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "JOB_TITLE")
@Getter
@Setter
@NoArgsConstructor
public class JobTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOB_TITLE_ID")
    private Integer jobTitleId;

    @NotBlank
    @Column(name = "JOB_TITLE_NAME", nullable = false, unique = true, length = 50)
    private String jobTitleName;

    @Column(name = "STATUS", nullable = false)
    private Byte status = 1;
}
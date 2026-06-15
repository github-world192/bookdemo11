package com.bookdemo11.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "member_name", nullable = false)
    private String memberName;

    @NotBlank
    @Email
    @Column(name = "member_email", nullable = false, unique = true)
    private String memberEmail;

    @NotBlank
    @Column(name = "member_password", nullable = false)
    private String memberPassword;

    @Size(max = 20)
    @Column(name = "member_phone")
    private String memberPhone;

    @Size(max = 200)
    @Column(name = "member_address")
    private String memberAddress;

    @Column(name = "member_birthday")
    private LocalDate memberBirthday;

    @Column(name = "member_level")
    private Integer memberLevel = 1;

    @Column(name = "member_status")
    private Integer memberStatus = 1;

    @Column(name = "register_date", insertable = false, updatable = false)
    private LocalDateTime registerDate;
}
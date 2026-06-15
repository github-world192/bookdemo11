package com.bookdemo11.coupon.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "min_amount")
    private Integer minAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "coupon_status")
    private Integer couponStatus = 1;
}
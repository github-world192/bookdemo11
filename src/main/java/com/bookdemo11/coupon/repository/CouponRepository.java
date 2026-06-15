package com.bookdemo11.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, String> {
}
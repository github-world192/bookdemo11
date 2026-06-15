package com.bookdemo11.shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.shop.entity.ShopOrder;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Integer> {
    List<ShopOrder> findByMemberMemberIdOrderByOrderDateDesc(Integer memberId);
    Optional<ShopOrder> findByStripeSessionId(String stripeSessionId);
}
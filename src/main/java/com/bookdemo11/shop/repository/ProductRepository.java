package com.bookdemo11.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.shop.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBySaleStatus(Integer saleStatus);
}
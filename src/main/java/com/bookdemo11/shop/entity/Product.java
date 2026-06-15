package com.bookdemo11.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @NotBlank
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_desc", length = 1000)
    private String productDesc;

    @Min(1)
    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "sale_status")
    private Integer saleStatus = 1;
}
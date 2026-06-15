package com.bookdemo11.shop.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PendingShopOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private Map<Integer, Integer> cart = new LinkedHashMap<>();
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String payMethod;
}
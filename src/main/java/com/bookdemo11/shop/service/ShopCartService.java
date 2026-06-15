package com.bookdemo11.shop.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bookdemo11.shop.entity.Product;
import com.bookdemo11.shop.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class ShopCartService {

    public static final String CART_SESSION_KEY = "shopCart";

    private final ProductRepository productRepository;

    public ShopCartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getCart(HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Integer productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (product.getSaleStatus() != 1) {
            throw new IllegalArgumentException("商品已下架");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("數量必須大於 0");
        }
        Map<Integer, Integer> cart = getCart(session);
        int newQty = cart.getOrDefault(productId, 0) + quantity;
        if (newQty > product.getStock()) {
            throw new IllegalArgumentException("庫存不足");
        }
        cart.put(productId, newQty);
    }

    public void updateQuantity(HttpSession session, Integer productId, int quantity) {
        Map<Integer, Integer> cart = getCart(session);
        if (quantity <= 0) {
            cart.remove(productId);
            return;
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("庫存不足");
        }
        cart.put(productId, quantity);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public int getCartItemCount(HttpSession session) {
        return getCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }
}
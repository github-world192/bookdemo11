package com.bookdemo11.shop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.member.entity.Member;
import com.bookdemo11.shop.dto.PendingShopOrder;
import com.bookdemo11.shop.entity.Product;
import com.bookdemo11.shop.entity.ShopOrder;
import com.bookdemo11.shop.entity.ShopOrderDetail;
import com.bookdemo11.shop.repository.ProductRepository;
import com.bookdemo11.shop.repository.ShopOrderRepository;

@Service
public class ShopOrderService {

    private final ShopOrderRepository shopOrderRepository;
    private final ProductRepository productRepository;

    public ShopOrderService(ShopOrderRepository shopOrderRepository, ProductRepository productRepository) {
        this.shopOrderRepository = shopOrderRepository;
        this.productRepository = productRepository;
    }

    public List<ShopOrder> findByMember(Integer memberId) {
        return shopOrderRepository.findByMemberMemberIdOrderByOrderDateDesc(memberId);
    }

    public Optional<ShopOrder> findByStripeSessionId(String sessionId) {
        return shopOrderRepository.findByStripeSessionId(sessionId);
    }

    public int calculateTotal(Map<Integer, Integer> cart) {
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
            total += product.getProductPrice() * entry.getValue();
        }
        return total;
    }

    public List<CheckoutLine> buildCheckoutLines(Map<Integer, Integer> cart) {
        List<CheckoutLine> lines = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
            if (entry.getValue() > product.getStock()) {
                throw new IllegalArgumentException(product.getProductName() + " 庫存不足");
            }
            lines.add(new CheckoutLine(product, entry.getValue()));
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("購物車是空的");
        }
        return lines;
    }

    @Transactional
    public ShopOrder createOrder(Member member, Map<Integer, Integer> cart,
                                 String recipientName, String recipientPhone, String recipientAddress,
                                 String payMethod, String stripeSessionId, boolean paid) {
        List<CheckoutLine> lines = buildCheckoutLines(cart);
        int total = lines.stream().mapToInt(l -> l.product().getProductPrice() * l.quantity()).sum();

        ShopOrder order = new ShopOrder();
        order.setMember(member);
        order.setTotalAmount(total);
        order.setActualAmount(total);
        order.setRecipientName(recipientName);
        order.setRecipientPhone(recipientPhone);
        order.setRecipientAddress(recipientAddress);
        order.setPayMethod(payMethod);
        order.setPayStatus(paid ? "1" : "0");
        order.setOrderStatus(1);
        order.setStripeSessionId(stripeSessionId);

        for (CheckoutLine line : lines) {
            ShopOrderDetail detail = new ShopOrderDetail();
            detail.setShopOrder(order);
            detail.setProduct(line.product());
            detail.setQuantity(line.quantity());
            detail.setUnitPrice(line.product().getProductPrice());
            order.getOrderDetails().add(detail);

            Product product = line.product();
            product.setStock(product.getStock() - line.quantity());
            productRepository.save(product);
        }

        return shopOrderRepository.save(order);
    }

    @Transactional
    public ShopOrder createOrderFromPending(Member member, PendingShopOrder pending, String stripeSessionId) {
        return createOrder(member, pending.getCart(), pending.getRecipientName(),
                pending.getRecipientPhone(), pending.getRecipientAddress(),
                "1", stripeSessionId, true);
    }

    public record CheckoutLine(Product product, int quantity) {}
}
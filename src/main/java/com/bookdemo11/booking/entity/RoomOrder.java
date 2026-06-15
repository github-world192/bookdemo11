package com.bookdemo11.booking.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bookdemo11.member.entity.Member;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_order")
@Getter
@Setter
@NoArgsConstructor
public class RoomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_order_id")
    private Integer roomOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "room_amount")
    private Integer roomAmount;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "actual_amount")
    private Integer actualAmount;

    @Column(name = "discount_amount")
    private Integer discountAmount = 0;

    @Column(name = "order_status")
    private Integer orderStatus = 0;

    @Column(name = "pay_method")
    private String payMethod = "0";

    @Column(name = "pay_status")
    private String payStatus = "0";

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(name = "special_req", length = 500)
    private String specialReq;

    @CreationTimestamp
    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "roomOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomOrderDetail> orderDetails = new ArrayList<>();
}
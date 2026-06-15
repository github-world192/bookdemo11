package com.bookdemo11.booking.entity;

import com.bookdemo11.room.entity.RoomType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_order_detail")
@Getter
@Setter
@NoArgsConstructor
public class RoomOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_order_id", nullable = false)
    private RoomOrder roomOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(name = "room_amount")
    private Integer roomAmount;

    @Column(name = "room_price")
    private Integer roomPrice;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "room_guest_name")
    private String roomGuestName;

    @Column(name = "special_req", length = 300)
    private String specialReq;

    @Column(name = "list_status")
    private Integer listStatus = 1;
}
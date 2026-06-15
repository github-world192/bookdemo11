package com.bookdemo11.booking.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookdemo11.booking.entity.RoomOrder;

public interface RoomOrderRepository extends JpaRepository<RoomOrder, Integer> {
    List<RoomOrder> findByMemberMemberIdOrderByOrderDateDesc(Integer memberId);

    @Query("""
            SELECT COALESCE(SUM(d.roomAmount), 0) FROM RoomOrderDetail d
            JOIN d.roomOrder o
            WHERE d.roomType.roomTypeId = :roomTypeId
              AND o.orderStatus IN (0, 1, 2)
              AND o.checkInDate < :checkOut
              AND o.checkOutDate > :checkIn
            """)
    int countBookedRooms(@Param("roomTypeId") Integer roomTypeId,
                         @Param("checkIn") LocalDate checkIn,
                         @Param("checkOut") LocalDate checkOut);

    List<RoomOrder> findAllByOrderByOrderDateDesc();

    Optional<RoomOrder> findByStripeSessionId(String stripeSessionId);
}
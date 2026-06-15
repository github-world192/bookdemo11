package com.bookdemo11.booking.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PendingBookingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String guestName;
    private String guestPhone;
    private String specialReq;
    private String couponCode;
    private List<BookingLine> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BookingLine implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer roomTypeId;
        private Integer quantity;
        private Integer guests;
    }
}
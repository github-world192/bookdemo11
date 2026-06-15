package com.bookdemo11.booking.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.booking.dto.OrderPreview;
import com.bookdemo11.booking.dto.PendingBookingRequest;
import com.bookdemo11.booking.entity.RoomOrder;
import com.bookdemo11.booking.entity.RoomOrderDetail;
import com.bookdemo11.booking.repository.RoomOrderRepository;
import com.bookdemo11.coupon.entity.Coupon;
import com.bookdemo11.coupon.repository.CouponRepository;
import com.bookdemo11.member.entity.Member;
import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.service.RoomTypeService;

@Service
public class RoomOrderService {

    private final RoomOrderRepository roomOrderRepository;
    private final RoomTypeService roomTypeService;
    private final CouponRepository couponRepository;

    public RoomOrderService(RoomOrderRepository roomOrderRepository,
                            RoomTypeService roomTypeService,
                            CouponRepository couponRepository) {
        this.roomOrderRepository = roomOrderRepository;
        this.roomTypeService = roomTypeService;
        this.couponRepository = couponRepository;
    }

    public List<RoomOrder> findByMember(Integer memberId) {
        return roomOrderRepository.findByMemberMemberIdOrderByOrderDateDesc(memberId);
    }

    public List<RoomOrder> findAll() {
        return roomOrderRepository.findAllByOrderByOrderDateDesc();
    }

    public Optional<RoomOrder> findById(Integer id) {
        return roomOrderRepository.findById(id);
    }

    public Optional<RoomOrder> findByStripeSessionId(String sessionId) {
        return roomOrderRepository.findByStripeSessionId(sessionId);
    }

    public OrderPreview previewOrder(LocalDate checkIn, LocalDate checkOut,
                                     List<BookingItem> items, String couponCode) {
        OrderBuildResult result = buildOrderDetails(checkIn, checkOut, items, couponCode);
        OrderPreview preview = new OrderPreview();
        preview.setTotalAmount(result.totalAmount());
        preview.setDiscountAmount(result.discount());
        preview.setActualAmount(Math.max(0, result.totalAmount() - result.discount()));
        preview.setRoomCount(result.roomCount());
        preview.setNights(result.nights());

        for (RoomOrderDetail detail : result.details()) {
            OrderPreview.PreviewLine line = new OrderPreview.PreviewLine();
            line.setName(detail.getRoomType().getRoomTypeName());
            line.setDescription("住宿 " + result.nights() + " 晚 x " + detail.getRoomAmount() + " 間");
            line.setUnitAmount(detail.getRoomPrice() * result.nights());
            line.setQuantity(detail.getRoomAmount());
            preview.getLines().add(line);
        }
        if (result.discount() > 0) {
            OrderPreview.PreviewLine discountLine = new OrderPreview.PreviewLine();
            discountLine.setName("優惠券折抵");
            discountLine.setDescription("coupon");
            discountLine.setUnitAmount(-result.discount());
            discountLine.setQuantity(1);
            preview.getLines().add(discountLine);
        }
        return preview;
    }

    @Transactional
    public RoomOrder createOrder(Member member, LocalDate checkIn, LocalDate checkOut,
                                 List<BookingItem> items, String guestName, String guestPhone,
                                 String specialReq, String payMethod, String couponCode) {
        return createOrder(member, checkIn, checkOut, items, guestName, guestPhone,
                specialReq, payMethod, couponCode, null, false);
    }

    @Transactional
    public RoomOrder createOrder(Member member, LocalDate checkIn, LocalDate checkOut,
                                 List<BookingItem> items, String guestName, String guestPhone,
                                 String specialReq, String payMethod, String couponCode,
                                 String stripeSessionId, boolean paid) {
        OrderBuildResult result = buildOrderDetails(checkIn, checkOut, items, couponCode);

        RoomOrder order = new RoomOrder();
        order.setMember(member);
        order.setCheckInDate(checkIn);
        order.setCheckOutDate(checkOut);
        order.setRoomAmount(result.roomCount());
        order.setTotalAmount(result.totalAmount());
        order.setDiscountAmount(result.discount());
        order.setActualAmount(Math.max(0, result.totalAmount() - result.discount()));
        order.setGuestName(guestName);
        order.setGuestPhone(guestPhone);
        order.setSpecialReq(specialReq);
        order.setPayMethod(payMethod);
        order.setPayStatus(paid ? "1" : "0");
        order.setOrderStatus(1);
        order.setStripeSessionId(stripeSessionId);

        for (RoomOrderDetail detail : result.details()) {
            detail.setRoomOrder(order);
            order.getOrderDetails().add(detail);
        }

        return roomOrderRepository.save(order);
    }

    @Transactional
    public RoomOrder createOrderFromPending(Member member, PendingBookingRequest pending,
                                            String stripeSessionId) {
        List<BookingItem> items = pending.getItems().stream()
                .map(l -> new BookingItem(l.getRoomTypeId(), l.getQuantity(), l.getGuests()))
                .toList();
        return createOrder(member, pending.getCheckIn(), pending.getCheckOut(), items,
                pending.getGuestName(), pending.getGuestPhone(), pending.getSpecialReq(),
                "1", pending.getCouponCode(), stripeSessionId, true);
    }

    public List<BookingItem> toBookingItems(PendingBookingRequest pending) {
        return pending.getItems().stream()
                .map(l -> new BookingItem(l.getRoomTypeId(), l.getQuantity(), l.getGuests()))
                .toList();
    }

    private OrderBuildResult buildOrderDetails(LocalDate checkIn, LocalDate checkOut,
                                               List<BookingItem> items, String couponCode) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("退房日期必須晚於入住日期");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        int totalAmount = 0;
        int roomCount = 0;
        List<RoomOrderDetail> details = new ArrayList<>();

        for (BookingItem item : items) {
            if (item.quantity() <= 0) {
                continue;
            }
            RoomType roomType = roomTypeService.findById(item.roomTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("房型不存在"));
            int available = roomTypeService.getAvailableCount(roomType.getRoomTypeId(), checkIn, checkOut);
            if (item.quantity() > available) {
                throw new IllegalArgumentException(roomType.getRoomTypeName() + " 剩餘空房不足");
            }

            RoomOrderDetail detail = new RoomOrderDetail();
            detail.setRoomType(roomType);
            detail.setRoomAmount(item.quantity());
            detail.setRoomPrice(roomType.getRoomTypePrice());
            detail.setNumberOfPeople(item.guests());
            details.add(detail);

            totalAmount += roomType.getRoomTypePrice() * item.quantity() * nights;
            roomCount += item.quantity();
        }

        if (details.isEmpty()) {
            throw new IllegalArgumentException("請至少選擇一間房型");
        }

        int discount = resolveDiscount(couponCode, totalAmount);
        return new OrderBuildResult(details, totalAmount, discount, roomCount, nights);
    }

    private int resolveDiscount(String couponCode, int totalAmount) {
        if (couponCode == null || couponCode.isBlank()) {
            return 0;
        }
        Coupon coupon = couponRepository.findById(couponCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("優惠券不存在"));
        if (coupon.getCouponStatus() != 1) {
            throw new IllegalArgumentException("優惠券已失效");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(coupon.getStartDate()) || today.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException("優惠券不在有效期限內");
        }
        if (totalAmount < coupon.getMinAmount()) {
            throw new IllegalArgumentException("訂單金額未達優惠券使用門檻");
        }
        return coupon.getDiscountAmount();
    }

    @Transactional
    public void updateStatus(Integer orderId, Integer status) {
        RoomOrder order = roomOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
        order.setOrderStatus(status);
        roomOrderRepository.save(order);
    }

    @Transactional
    public void checkIn(Integer orderId) {
        updateStatus(orderId, 2);
    }

    @Transactional
    public void checkOut(Integer orderId) {
        updateStatus(orderId, 3);
        RoomOrder order = roomOrderRepository.findById(orderId).orElseThrow();
        order.setPayStatus("1");
        roomOrderRepository.save(order);
    }

    public record BookingItem(Integer roomTypeId, Integer quantity, Integer guests) {}

    private record OrderBuildResult(List<RoomOrderDetail> details, int totalAmount,
                                    int discount, int roomCount, long nights) {}
}
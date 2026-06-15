package com.bookdemo11.booking.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.booking.dto.OrderPreview;
import com.bookdemo11.booking.dto.PendingBookingRequest;
import com.bookdemo11.booking.entity.RoomOrder;
import com.bookdemo11.booking.service.RoomOrderService;
import com.bookdemo11.booking.service.RoomOrderService.BookingItem;
import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;
import com.bookdemo11.payment.service.StripePaymentService;
import com.bookdemo11.payment.service.StripePaymentService.LineItem;
import com.bookdemo11.room.service.RoomTypeService;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private static final String PENDING_BOOKING_KEY = "pendingBooking";

    private final RoomTypeService roomTypeService;
    private final RoomOrderService roomOrderService;
    private final MemberRepository memberRepository;
    private final StripePaymentService stripePaymentService;

    public BookingController(RoomTypeService roomTypeService,
                             RoomOrderService roomOrderService,
                             MemberRepository memberRepository,
                             StripePaymentService stripePaymentService) {
        this.roomTypeService = roomTypeService;
        this.roomOrderService = roomOrderService;
        this.memberRepository = memberRepository;
        this.stripePaymentService = stripePaymentService;
    }

    @GetMapping("/search")
    public String searchForm(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") Integer guests,
            @RequestParam(required = false) Integer roomTypeId,
            Model model) {

        Map<Integer, Integer> availability = roomTypeService.getAvailabilityMap(checkIn, checkOut);
        model.addAttribute("roomTypes", roomTypeService.getAvailableRoomTypes());
        model.addAttribute("availability", availability);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);
        model.addAttribute("preselectedRoomTypeId", roomTypeId);
        model.addAttribute("nights", ChronoUnit.DAYS.between(checkIn, checkOut));
        model.addAttribute("stripeEnabled", stripePaymentService.isConfigured());
        return "booking/search";
    }

    @PostMapping("/confirm")
    public String confirmBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam String guestName,
            @RequestParam String guestPhone,
            @RequestParam(required = false) String specialReq,
            @RequestParam(defaultValue = "0") String payMethod,
            @RequestParam(required = false) String couponCode,
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        List<BookingItem> items = parseBookingItems(params);

        try {
            if ("1".equals(payMethod)) {
                return startStripeCheckout(member, checkIn, checkOut, guestName, guestPhone,
                        specialReq, couponCode, items, session, redirectAttributes);
            }

            RoomOrder order = roomOrderService.createOrder(
                    member, checkIn, checkOut, items, guestName, guestPhone,
                    specialReq, payMethod, couponCode);
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/booking/success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rooms?checkIn=" + checkIn + "&checkOut=" + checkOut;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stripe 付款啟動失敗：" + e.getMessage());
            return "redirect:/booking/search?checkIn=" + checkIn + "&checkOut=" + checkOut;
        }
    }

    @GetMapping("/stripe/success")
    public String stripeSuccess(@RequestParam("session_id") String sessionId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {
        try {
            Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();

            if (roomOrderService.findByStripeSessionId(sessionId).isPresent()) {
                redirectAttributes.addFlashAttribute("order",
                        roomOrderService.findByStripeSessionId(sessionId).get());
                return "redirect:/booking/success";
            }

            Session session = stripePaymentService.retrieveSession(sessionId);
            if (!stripePaymentService.isSessionPaid(session)) {
                redirectAttributes.addFlashAttribute("errorMessage", "付款尚未完成");
                return "redirect:/booking/records";
            }

            PendingBookingRequest pending = (PendingBookingRequest) httpSession.getAttribute(PENDING_BOOKING_KEY);
            if (pending == null || !member.getMemberId().equals(pending.getMemberId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "訂房資料已過期，請重新下單");
                return "redirect:/rooms";
            }

            RoomOrder order = roomOrderService.createOrderFromPending(member, pending, sessionId);
            httpSession.removeAttribute(PENDING_BOOKING_KEY);
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/booking/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "付款確認失敗：" + e.getMessage());
            return "redirect:/booking/records";
        }
    }

    @GetMapping("/stripe/cancel")
    public String stripeCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "已取消 Stripe 付款");
        return "redirect:/rooms";
    }

    @GetMapping("/success")
    public String bookingSuccess(Model model) {
        if (!model.containsAttribute("order")) {
            return "redirect:/member/center";
        }
        return "booking/success";
    }

    @GetMapping("/records")
    public String bookingRecords(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("orders", roomOrderService.findByMember(member.getMemberId()));
        return "booking/records";
    }

    private String startStripeCheckout(Member member, LocalDate checkIn, LocalDate checkOut,
                                       String guestName, String guestPhone, String specialReq,
                                       String couponCode, List<BookingItem> items,
                                       HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
        OrderPreview preview = roomOrderService.previewOrder(checkIn, checkOut, items, couponCode);

        PendingBookingRequest pending = new PendingBookingRequest();
        pending.setMemberId(member.getMemberId());
        pending.setCheckIn(checkIn);
        pending.setCheckOut(checkOut);
        pending.setGuestName(guestName);
        pending.setGuestPhone(guestPhone);
        pending.setSpecialReq(specialReq);
        pending.setCouponCode(couponCode);
        for (BookingItem item : items) {
            PendingBookingRequest.BookingLine line = new PendingBookingRequest.BookingLine();
            line.setRoomTypeId(item.roomTypeId());
            line.setQuantity(item.quantity());
            line.setGuests(item.guests());
            pending.getItems().add(line);
        }
        session.setAttribute(PENDING_BOOKING_KEY, pending);

        List<LineItem> stripeLines = new ArrayList<>();
        if (preview.getDiscountAmount() > 0) {
            stripeLines.add(new LineItem(
                    "The Star 訂房（含優惠折抵）",
                    "入住 " + checkIn + " → " + checkOut,
                    preview.getActualAmount(),
                    1));
        } else {
            for (OrderPreview.PreviewLine line : preview.getLines()) {
                stripeLines.add(new LineItem(
                        line.getName(),
                        line.getDescription(),
                        line.getUnitAmount(),
                        line.getQuantity()));
            }
        }

        String cancelUrl = "/booking/stripe/cancel";
        Session checkout = stripePaymentService.createCheckoutSession(
                "booking",
                "/booking/stripe/success",
                cancelUrl,
                member.getMemberEmail(),
                stripeLines,
                Map.of("member_id", String.valueOf(member.getMemberId())));

        return "redirect:" + checkout.getUrl();
    }

    private List<BookingItem> parseBookingItems(Map<String, String> params) {
        List<BookingItem> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("qty_")) {
                String roomTypeIdStr = entry.getKey().substring(4);
                int qty = Integer.parseInt(entry.getValue());
                if (qty > 0) {
                    int guests = Integer.parseInt(params.getOrDefault("guests_" + roomTypeIdStr, "2"));
                    items.add(new BookingItem(Integer.parseInt(roomTypeIdStr), qty, guests));
                }
            }
        }
        return items;
    }
}
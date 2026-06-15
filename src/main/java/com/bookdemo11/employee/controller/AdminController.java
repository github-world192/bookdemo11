package com.bookdemo11.employee.controller;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.booking.service.RoomOrderService;
import com.bookdemo11.member.service.MemberService;
import com.bookdemo11.room.entity.Room;
import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.service.RoomService;
import com.bookdemo11.room.service.RoomTypeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoomTypeService roomTypeService;
    private final RoomService roomService;
    private final RoomOrderService roomOrderService;
    private final MemberService memberService;

    public AdminController(RoomTypeService roomTypeService,
                           RoomService roomService,
                           RoomOrderService roomOrderService,
                           MemberService memberService) {
        this.roomTypeService = roomTypeService;
        this.roomService = roomService;
        this.roomOrderService = roomOrderService;
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("@adminSecurity.hasPermission('DASHBOARD_VIEW')")
    public String dashboard(Model model) {
        model.addAttribute("orderCount", roomOrderService.findAll().size());
        model.addAttribute("roomTypeCount", roomTypeService.findAll().size());
        model.addAttribute("memberCount", memberService.findAll().size());
        model.addAttribute("recentOrders", roomOrderService.findAll().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "admin/access-denied";
    }

    @GetMapping("/room-types")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_TYPE_MANAGE')")
    public String roomTypes(Model model) {
        model.addAttribute("roomTypes", roomTypeService.findAll());
        model.addAttribute("roomType", new RoomType());
        return "admin/room-types";
    }

    @PostMapping("/room-types")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_TYPE_MANAGE')")
    public String saveRoomType(@Valid @ModelAttribute("roomType") RoomType roomType,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypes", roomTypeService.findAll());
            return "admin/room-types";
        }
        roomTypeService.save(roomType);
        redirectAttributes.addFlashAttribute("successMessage", "房型已儲存");
        return "redirect:/admin/room-types";
    }

    @PostMapping("/room-types/{id}/delete")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_TYPE_MANAGE')")
    public String deleteRoomType(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomTypeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "房型已刪除");
        return "redirect:/admin/room-types";
    }

    @GetMapping("/rooms")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_MANAGE')")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("roomTypes", roomTypeService.findAll());
        model.addAttribute("room", new Room());
        return "admin/rooms";
    }

    @PostMapping("/rooms")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_MANAGE')")
    public String saveRoom(@RequestParam Integer roomTypeId,
                           @ModelAttribute Room room,
                           RedirectAttributes redirectAttributes) {
        RoomType roomType = roomTypeService.findById(roomTypeId).orElseThrow();
        roomService.save(room, roomType);
        redirectAttributes.addFlashAttribute("successMessage", "房間已儲存");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/delete")
    @PreAuthorize("@adminSecurity.hasPermission('ROOM_MANAGE')")
    public String deleteRoom(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "房間已刪除");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/orders")
    @PreAuthorize("@adminSecurity.hasPermission('ORDER_MANAGE')")
    public String orders(Model model) {
        model.addAttribute("orders", roomOrderService.findAll());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/checkin")
    @PreAuthorize("@adminSecurity.hasPermission('ORDER_MANAGE')")
    public String checkIn(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomOrderService.checkIn(id);
        redirectAttributes.addFlashAttribute("successMessage", "已完成 Check-in");
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/checkout")
    @PreAuthorize("@adminSecurity.hasPermission('ORDER_MANAGE')")
    public String checkOut(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomOrderService.checkOut(id);
        redirectAttributes.addFlashAttribute("successMessage", "已完成 Check-out");
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/cancel")
    @PreAuthorize("@adminSecurity.hasPermission('ORDER_MANAGE')")
    public String cancelOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        roomOrderService.updateStatus(id, 4);
        redirectAttributes.addFlashAttribute("successMessage", "訂單已取消");
        return "redirect:/admin/orders";
    }

    @GetMapping("/members")
    @PreAuthorize("@adminSecurity.hasPermission('MEMBER_MANAGE')")
    public String members(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "admin/members";
    }
}
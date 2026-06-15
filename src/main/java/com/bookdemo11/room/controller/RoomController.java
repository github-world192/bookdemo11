package com.bookdemo11.room.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.service.RoomTypeService;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomTypeService roomTypeService;

    public RoomController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public String listRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        LocalDate in = checkIn != null ? checkIn : LocalDate.now().plusDays(1);
        LocalDate out = checkOut != null ? checkOut : in.plusDays(1);
        if (!out.isAfter(in)) {
            out = in.plusDays(1);
        }

        Map<Integer, Integer> availability = roomTypeService.getAvailabilityMap(in, out);
        model.addAttribute("roomTypes", roomTypeService.getAvailableRoomTypes());
        model.addAttribute("availability", availability);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        return "room/list";
    }

    @GetMapping("/{id}")
    public String roomDetail(@PathVariable Integer id,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                             Model model) {
        RoomType roomType = roomTypeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("房型不存在"));

        LocalDate in = checkIn != null ? checkIn : LocalDate.now().plusDays(1);
        LocalDate out = checkOut != null ? checkOut : in.plusDays(1);
        if (!out.isAfter(in)) {
            out = in.plusDays(1);
        }

        model.addAttribute("roomType", roomType);
        model.addAttribute("available", roomTypeService.getAvailableCount(id, in, out));
        model.addAttribute("totalRooms", roomTypeService.getTotalRooms(id));
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        return "room/detail";
    }
}
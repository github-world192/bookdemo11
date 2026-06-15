package com.bookdemo11.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookdemo11.room.service.RoomTypeService;

@Controller
public class HomeController {

    private final RoomTypeService roomTypeService;

    public HomeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("hotelName", "The Star");
        model.addAttribute("hotelSubtitle", "東方之星度假飯店");
        model.addAttribute("roomTypes", roomTypeService.getAvailableRoomTypes());
        return "front/index";
    }

    @GetMapping("/about")
    public String about() {
        return "front/about";
    }

    @GetMapping("/facilities")
    public String facilities() {
        return "front/facilities";
    }

    @GetMapping("/faq")
    public String faq() {
        return "front/faq";
    }
}
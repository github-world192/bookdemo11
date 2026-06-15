package com.bookdemo11.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.booking.service.RoomOrderService;
import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;
import com.bookdemo11.member.service.MemberService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RoomOrderService roomOrderService;

    public MemberController(MemberService memberService,
                            MemberRepository memberRepository,
                            RoomOrderService roomOrderService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.roomOrderService = roomOrderService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("member", new Member());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("member") Member member,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "member/register";
        }
        try {
            memberService.register(member);
            redirectAttributes.addFlashAttribute("successMessage", "註冊成功，請登入");
            return "redirect:/member/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("memberEmail", "duplicate", e.getMessage());
            return "member/register";
        }
    }

    @GetMapping("/center")
    public String memberCenter(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("member", member);
        model.addAttribute("orders", roomOrderService.findByMember(member.getMemberId()));
        return "member/center";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute Member form,
                                RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        member.setMemberName(form.getMemberName());
        member.setMemberPhone(form.getMemberPhone());
        member.setMemberAddress(form.getMemberAddress());
        member.setMemberBirthday(form.getMemberBirthday());
        memberService.updateProfile(member);
        redirectAttributes.addFlashAttribute("successMessage", "個人資料已更新");
        return "redirect:/member/center";
    }
}
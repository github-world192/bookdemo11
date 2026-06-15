package com.bookdemo11.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;

public final class MemberPrincipal {

    private MemberPrincipal() {}

    public static Member getCurrentMember(MemberRepository memberRepository) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return memberRepository.findByMemberEmail(auth.getName()).orElse(null);
    }
}
package com.bookdemo11.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByMemberEmail(email);
    }

    public Optional<Member> findById(Integer id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public Member register(Member member) {
        if (memberRepository.existsByMemberEmail(member.getMemberEmail())) {
            throw new IllegalArgumentException("此信箱已被註冊");
        }
        member.setMemberPassword(passwordEncoder.encode(member.getMemberPassword()));
        member.setMemberStatus(1);
        member.setMemberLevel(1);
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateProfile(Member member) {
        return memberRepository.save(member);
    }
}
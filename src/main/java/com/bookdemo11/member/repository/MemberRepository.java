package com.bookdemo11.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberEmail(String memberEmail);
    boolean existsByMemberEmail(String memberEmail);
}
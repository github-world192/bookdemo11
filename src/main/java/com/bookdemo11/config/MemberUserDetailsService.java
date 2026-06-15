package com.bookdemo11.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到會員帳號"));

        if (member.getMemberStatus() != null && member.getMemberStatus() == 2) {
            throw new UsernameNotFoundException("此帳號已被停權");
        }

        return new User(
                member.getMemberEmail(),
                member.getMemberPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }
}
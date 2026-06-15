package com.bookdemo11.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final MemberUserDetailsService memberUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler;

    public SecurityConfig(MemberUserDetailsService memberUserDetailsService,
                          AdminUserDetailsService adminUserDetailsService,
                          AdminAuthenticationSuccessHandler adminAuthenticationSuccessHandler) {
        this.memberUserDetailsService = memberUserDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.adminAuthenticationSuccessHandler = adminAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/admin/access-denied").permitAll()
                .requestMatchers("/admin/dashboard").hasAnyAuthority("ROLE_ADMIN", "DASHBOARD_VIEW")
                .requestMatchers("/admin/employees/**").hasAnyAuthority("ROLE_ADMIN", "EMPLOYEE_MANAGE")
                .requestMatchers("/admin/org/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGE")
                .requestMatchers("/admin/members/**").hasAnyAuthority("ROLE_ADMIN", "MEMBER_MANAGE")
                .requestMatchers("/admin/room-types/**").hasAnyAuthority("ROLE_ADMIN", "ROOM_TYPE_MANAGE")
                .requestMatchers("/admin/rooms/**").hasAnyAuthority("ROLE_ADMIN", "ROOM_MANAGE")
                .requestMatchers("/admin/orders/**").hasAnyAuthority("ROLE_ADMIN", "ORDER_MANAGE")
                .requestMatchers("/admin/**").authenticated()
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex.accessDeniedHandler(adminAccessDeniedHandler()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/admin/login?expired=true"))
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(adminAuthenticationSuccessHandler)
                .failureUrl("/admin/login?error=true")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .permitAll())
            .userDetailsService(adminUserDetailsService);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain memberSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/rooms/**", "/shop", "/about", "/facilities", "/faq",
                    "/css/**", "/js/**", "/images/**", "/h2-console/**",
                    "/member/register", "/member/login").permitAll()
                .requestMatchers("/member/**", "/booking/**", "/shop/**").hasRole("MEMBER")
                .anyRequest().permitAll())
            .formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/member/login?error=true")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/home?logout=true")
                .permitAll())
            .userDetailsService(memberUserDetailsService)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    private AccessDeniedHandler adminAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.access.AccessDeniedException ex) ->
                response.sendRedirect(request.getContextPath() + "/admin/access-denied");
    }
}
//package com.univus.project.config;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final UserDetailsService customUserDetailsService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/join").permitAll()
//                        .anyRequest().authenticated()
//                )
//
//                .formLogin(form -> form
//                        .loginPage("/login")                // 직접 만든 로그인 페이지 URL
//                        .loginProcessingUrl("/login")       // 로그인 요청을 처리하는 URL
//                        .defaultSuccessUrl("/home", true)   // 로그인 성공 후 이동
//                        .usernameParameter("email")         // username 파라미터명 지정
//                        .passwordParameter("password")
//                        .permitAll()
//                )
//
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                )
//
//                .userDetailsService(customUserDetailsService);
//
//        return http.build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
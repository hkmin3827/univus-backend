package com.univus.project.controller;

import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.config.CustomUserDetails;
import com.univus.project.exception.AuthException;
import com.univus.project.repository.UserRepository;
import com.univus.project.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserRepository userRepository;

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResDto> login(@RequestBody LoginReqDto dto,
                                            HttpServletRequest request) {


        // 1) 이메일 존재 여부 먼저 확인
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("존재하지 않는 이메일입니다."));

        // 2) Security 인증 시도
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPwd());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            // 비밀번호가 틀린 경우
            throw new AuthException("비밀번호가 일치하지 않습니다.");
        }

        // 3) 인증 정보 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4) 세션에 SecurityContext 저장 (세션 기반 로그인 유지)
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        // 5) 인증된 사용자 정보 꺼내기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User authUser = userDetails.getUser();

        UserResDto res = new UserResDto(
                authUser.getId(),
                authUser.getEmail(),
                authUser.getName(),
                authUser.getRole(),
                authUser.getImage(),
                authUser.getRegDate(),
                authUser.isActive()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> userExists(@PathVariable String email) {
        boolean exists = authService.isUser(email);
        return ResponseEntity.ok(exists);
    }

    // ✅ 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody UserSignUpReqDto dto) {

        Long id = authService.signup(dto);
        return ResponseEntity.ok(id);
    }

    // ✅ 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        // 1. 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
//            log.info("로그아웃: 세션 무효화, id={}", session.getId());
            session.invalidate();
        }

        // 2. JSESSIONID 쿠키 삭제(선택이지만 해두면 깔끔)
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0) // 즉시 만료
                .httpOnly(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }


}

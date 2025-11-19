package com.univus.project.controller;

import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.config.CustomUserDetails;
import com.univus.project.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResDto> login(@RequestBody LoginReqDto dto,
                                            HttpServletRequest request) {

        // email + pwd 로 인증 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPwd());

        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션 생성
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        // 인증된 사용자 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 클라이언트로 반환할 DTO
        UserResDto res = new UserResDto(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getImage(),
                user.getRegDate()
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
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }


}

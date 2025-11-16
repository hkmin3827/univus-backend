package com.univus.project.controller;
// 회원 가입

import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    // 회원 가입 여부 확인x
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> userExists(@PathVariable String email) {
        log.info("email: {}", email);
        boolean isTrue = authService.isUser(email);
        return ResponseEntity.ok(isTrue);
    }

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody UserSignUpReqDto userSignUpReqDto) {
        log.info("userSignUpReqDto: {}", userSignUpReqDto.getEmail());
        log.info("userSignUpReqDto: {}", userSignUpReqDto.getPwd());
        log.info("userSignUpReqDto: {}", userSignUpReqDto.getName());
        return ResponseEntity.ok(authService.signup(userSignUpReqDto));
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginReqDto loginReqDto) {
        log.info("loginReqDto: {}", loginReqDto.getEmail());
        log.info("loginReqDto: {}", loginReqDto.getPwd());
        boolean isTrue = authService.login(loginReqDto);
        return ResponseEntity.ok(isTrue);
    }
}

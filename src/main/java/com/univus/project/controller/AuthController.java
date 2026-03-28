package com.univus.project.controller;

import com.univus.project.dto.auth.*;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.config.CustomUserDetails;
import com.univus.project.exception.AuthException;
import com.univus.project.repository.UserRepository;
import com.univus.project.service.AuthService;

import com.univus.project.service.PasswordResetService;
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
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<UserResDto> login(@RequestBody LoginReqDto dto,
                                            HttpServletRequest request) {


        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("존재하지 않는 이메일입니다."));

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPwd());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new AuthException("비밀번호가 일치하지 않습니다.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User authUser = userDetails.getUser();

        UserResDto res = new UserResDto(
                authUser
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> userExists(@PathVariable String email) {
        boolean exists = authService.isUser(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody UserSignUpReqDto dto) {

        Long id = authService.signup(dto);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponseDto> requestReset(@RequestBody PasswordResetRequestDto dto) {
        ApiResponseDto res = passwordResetService.requestReset(dto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/password-reset/validate")
    public ResponseEntity<ApiResponseDto> validateToken(@RequestParam String token) {
        ApiResponseDto res = passwordResetService.validateToken(token);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<ApiResponseDto> resetPassword(@RequestBody PasswordResetConfirmDto dto) {
        ApiResponseDto res = passwordResetService.resetPassword(dto);
        return ResponseEntity.ok(res);
    }
}

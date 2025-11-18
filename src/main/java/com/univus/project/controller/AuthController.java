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




//    Security 추가시 작성 변경?

//    private final AuthenticationManager authenticationManager;
//    private final UserService userService;
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto dto, HttpServletRequest request) {
//        UsernamePasswordAuthenticationToken token =
//                new UsernamePasswordAuthenticationToken(dto.getLoginId(), dto.getPassword());
//
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // 세션 생성(기본)
//        HttpSession session = request.getSession(true);
//        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
//
//        User user = ((com.univus.project.config.CustomUserDetails) authentication.getPrincipal()).getUser();
//
//        return ResponseEntity.ok(new AuthResponseDto(user.getId(), user.getLoginId(), user.getName()));
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<Long> register(@RequestBody AuthRequestDto dto) {
//        Long id = userService.register(dto.getLoginId(), dto.getPassword(), dto.getName());
//        return ResponseEntity.ok(id);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(HttpServletRequest request) {
//        request.getSession(false).invalidate();
//        SecurityContextHolder.clearContext();
//        return ResponseEntity.ok().build();
//    }
}

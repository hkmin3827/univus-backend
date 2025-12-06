// src/main/java/com/univus/project/service/PasswordResetService.java
package com.univus.project.service;

import com.univus.project.dto.auth.ApiResponseDto;
import com.univus.project.dto.auth.PasswordResetConfirmDto;
import com.univus.project.dto.auth.PasswordResetRequestDto;
import com.univus.project.entity.PasswordResetToken;
import com.univus.project.entity.User;
import com.univus.project.repository.PasswordResetTokenRepository;
import com.univus.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    // React 기준으로 이 링크를 보낼거야
    private static final String FRONT_RESET_URL = "http://localhost:3000/auth/reset-password/";

    // 1) 비밀번호 재설정 요청
    public ApiResponseDto requestReset(PasswordResetRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken =
                PasswordResetToken.create(user, token, Duration.ofMinutes(30)); // 30분 유효
        tokenRepository.save(resetToken);

        String resetLink = FRONT_RESET_URL + token;
        mailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return new ApiResponseDto("비밀번호 재설정 링크를 이메일로 전송했습니다.");
    }

    // 2) 토큰 유효성 체크 (프론트에서 페이지 열릴 때)
    public ApiResponseDto validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }
        if (resetToken.isUsed()) {
            throw new IllegalStateException("이미 사용된 토큰입니다.");
        }

        return new ApiResponseDto("유효한 토큰입니다.");
    }

    // 3) 실제 비밀번호 변경
    public ApiResponseDto resetPassword(PasswordResetConfirmDto dto) {
        PasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }
        if (resetToken.isUsed()) {
            throw new IllegalStateException("이미 사용된 토큰입니다.");
        }

        User user = resetToken.getUser();
        user.setPwd(passwordEncoder.encode(dto.getNewPassword()));

        resetToken.setUsed(true); // 재사용 방지

        // 굳이 delete는 안 해도 되지만, 정리하고 싶으면:
        // tokenRepository.delete(resetToken);

        return new ApiResponseDto("비밀번호가 성공적으로 변경되었습니다.");
    }
}

package com.univus.project.service;

import com.univus.project.constant.Role;
import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

// 회원가입, 로그인
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 여부
    public boolean isUser(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 가입
    public Long signup(UserSignUpReqDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPwd(passwordEncoder.encode(dto.getPwd()));  // 🔥 여기 중요!!
        user.setRole(Role.STUDENT);

        userRepository.save(user);
        return user.getId();
    }

    public Long login(LoginReqDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        // 🔥 raw: dto.getPwd(), encoded: user.getPwd()
        if (!passwordEncoder.matches(dto.getPwd(), user.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user.getId();
    }



    private User convertDtoToEntity(UserSignUpReqDto userSignUpReqDto) {
        User user = new User();
        user.setEmail(userSignUpReqDto.getEmail());
        user.setPwd(userSignUpReqDto.getPwd());
        user.setName(userSignUpReqDto.getName());
        user.setImage(userSignUpReqDto.getImage());
        user.setRole(userSignUpReqDto.getRole());
        return user;

    }
}

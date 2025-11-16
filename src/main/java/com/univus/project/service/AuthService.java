package com.univus.project.service;

import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 회원 가입 여부
    public boolean isUser(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 가입
    public boolean signup(UserSignUpReqDto userSignUpReqDto) {
        try {
            User user = convertDtoToEntity(userSignUpReqDto);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("회원 가입 시 오류 발생 : {}", e.getMessage());
            return false;
        }
    }

    public boolean login(LoginReqDto loginReqDto) {
        Optional<User> user = userRepository
                .findByEmailAndPwd(loginReqDto.getEmail(), loginReqDto.getPwd());
        return user.isPresent();
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

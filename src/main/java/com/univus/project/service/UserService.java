package com.univus.project.service;

import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResDto> findAll() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(User::isActive)
                .toList();

        List<UserResDto> userResDtos = new ArrayList<>();
        for (User user : users) {
            userResDtos.add(covertEntityToDto(user));
        }
        return userResDtos;
    }

    public UserResDto findByEmail(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return new UserResDto(user);
    }
    public UserResDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return new UserResDto(user);
    }

    public void updateUserProfile(String email, UserModifyReqDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            user.setImage(dto.getImage());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getProfile() != null && !dto.getProfile().isBlank()) {
            user.setProfile(dto.getProfile());
        }

        log.info("공통 프로필 수정 완료, userId={}", email);
    }

    public boolean withdrawUser(String email) {
        try {
            User user = userRepository.findByEmailAndActiveTrue(email)
                    .orElseThrow(() -> new RuntimeException("이미 탈퇴했거나 존재하지 않는 회원입니다."));

            user.setActive(false);

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("회원 탈퇴 실패 : {}", e.getMessage());
            return false;
        }
    }

    public List<UserResDto> findAllByAdmin() {
        List<User> users = userRepository.findAll()
                .stream()
                .toList();

        List<UserResDto> userResDtos = new ArrayList<>();
        for (User user : users) {
            userResDtos.add(covertEntityToDto(user));
        }
        return userResDtos;
    }

    public UserResDto findByEmailByAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return covertEntityToDto(user);
    }

    public boolean deleteUserByAdmin(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            log.error("관리자 회원 삭제 실패 : {}", e.getMessage());
            return false;
        }
    }

    public boolean withdrawByAdmin(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
            
            user.setActive(false);

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("탈퇴 실패 : {}", e.getMessage());
            return false;
        }
    }

    public boolean recoverUserByAdmin(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

            user.setActive(true);

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("회원 복구 실패 : {}", e.getMessage());
            return false;
        }
    }

    private UserResDto covertEntityToDto(User user) {
        UserResDto userResDto = new UserResDto();
        userResDto.setEmail(user.getEmail());
        userResDto.setName(user.getName());
        userResDto.setImage(user.getImage());
        userResDto.setRegDate(user.getRegDate());
        userResDto.setRole(user.getRole());
        userResDto.setActive(user.isActive());
        return userResDto;
    }
}

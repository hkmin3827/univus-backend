package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공통 수행 기능
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    // 회원 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<UserResDto>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 개별 회원 조회 (email)
    @GetMapping("/{email}")
    public ResponseEntity<UserResDto> getUser(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }



    // 유저 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdrawUser(@PathVariable String email) {
        boolean result = userService.withdrawUser(email);

        if (result) {
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.status(400).body("회원 탈퇴에 실패했습니다.");
        }
    }


    // 관리자 삭제
    @DeleteMapping("/admin/{email}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable String email) {
        boolean result = userService.deleteUserByAdmin(email);

        if (result) {
            return ResponseEntity.ok("관리자에 의해 계정이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(400).body("회원 삭제에 실패했습니다.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResDto> getCurrentUser(Authentication authentication) {
        // 인증된 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        UserResDto dto = new UserResDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),  
                user.getImage(),
                user.getRegDate(),
                user.getRole()
        );

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUserProfile(@PathVariable String email,
                                                  @RequestBody UserModifyReqDto dto) {
        log.info("공통 프로필 수정 요청 email={}, dto={}", email, dto);
        userService.updateUserProfile(email, dto);
        return ResponseEntity.ok().build();
    }
}

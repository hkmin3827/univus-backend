package com.univus.project.controller;

import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공통 수행 기능
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
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

    // 회원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateMember(@RequestBody UserModifyReqDto userModifyReqDto) {
        return ResponseEntity.ok(userService.updateUser(userModifyReqDto));
    }

    // 유저 탈퇴
    @DeleteMapping("/users/withdraw")
    public ResponseEntity<String> withdrawUser(@PathVariable String email) {
        boolean result = userService.withdrawUser(email);

        if (result) {
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.status(400).body("회원 탈퇴에 실패했습니다.");
        }
    }


    // 관리자 삭제
    @DeleteMapping("/admin/users/{email}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable String email) {
        boolean result = userService.deleteUserByAdmin(email);

        if (result) {
            return ResponseEntity.ok("관리자에 의해 계정이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(400).body("회원 삭제에 실패했습니다.");
        }
    }
}

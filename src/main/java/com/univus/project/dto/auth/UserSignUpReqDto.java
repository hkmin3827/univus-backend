package com.univus.project.dto.auth;

import com.univus.project.constant.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class UserSignUpReqDto {
    private String email;
    private String pwd;
    private String name;
    private String image;
    private String phone;
    private Role role;   // STUDENT / PROFESSOR

    // ------------------
    // 학생 전용 필드
    // ------------------
    private String studentNumber;
    private String major;
    private Integer grade;

    // ------------------
    // 교수 전용 필드
    // ------------------
    private String department;
    private String position;
}

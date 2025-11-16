package com.univus.project.dto.auth;
// 회원 가입

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
    private Role role;
}

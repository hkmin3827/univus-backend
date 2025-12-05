package com.univus.project.dto.auth;
// 게시물 수정

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginReqDto {
    private String email;
    private String pwd;
}

package com.univus.project.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyReqDto {
    private String email;
    private String name;    // 이름 수정
    private String image;   // 프로필 이미지 수정
    private String profile;
    private String phone;
}

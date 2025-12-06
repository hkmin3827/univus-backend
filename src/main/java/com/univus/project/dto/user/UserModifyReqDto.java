package com.univus.project.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyReqDto {
    private String email;
    private String name;    // 이름 수정
    private String image;   // 프로필 이미지 수정
    private String profile;

    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하고 11자리여야 합니다.")
    private String phone;
}

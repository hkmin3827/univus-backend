package com.univus.project.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ProfessorSignUpReqDto {
    private String department;
    private String phone;
    private String profile;
}

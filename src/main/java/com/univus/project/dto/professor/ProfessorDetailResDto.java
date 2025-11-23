package com.univus.project.dto.professor;

import com.univus.project.dto.user.UserResDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProfessorDetailResDto {

    private UserResDto user;   // 공통 정보

    // 교수 전용 필드
    private String department;
    private String position;
}

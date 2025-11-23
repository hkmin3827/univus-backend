package com.univus.project.dto.student;

import com.univus.project.dto.user.UserResDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentDetailResDto {

    private UserResDto user;   // 공통 정보

    // 학생 전용 필드
    private String studentNumber;
    private String major;
    private Integer grade;
}

package com.univus.project.dto.student;

import com.univus.project.dto.user.UserResDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentDetailResDto {

    private UserResDto user;

    private String studentNumber;
    private String major;
    private Integer grade;
}

package com.univus.project.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentModifyReqDto {
    public class StudentProfileModifyReqDto {
        private String major;
        private String studentNumber;
        private Integer grade;
    }
}

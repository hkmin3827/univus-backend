package com.univus.project.dto.professor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 교수 전용
public class ProfessorModifyReqDto {
    private String department;
    private String position;
}

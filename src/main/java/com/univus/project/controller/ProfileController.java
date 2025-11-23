package com.univus.project.controller;

import com.univus.project.dto.professor.ProfessorDetailResDto;
import com.univus.project.dto.professor.ProfessorModifyReqDto;
import com.univus.project.dto.student.StudentDetailResDto;
import com.univus.project.dto.student.StudentModifyReqDto;
import com.univus.project.service.ProfessorService;
import com.univus.project.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private final StudentService studentService;
    private final ProfessorService professorService;

    // 학생 상세 정보 조회
    @GetMapping("/students/{email}")
    public ResponseEntity<StudentDetailResDto> getStudentDetail(@PathVariable String email) {
        StudentDetailResDto dto = studentService.getStudentDetailByEmail(email);
        return ResponseEntity.ok(dto);
    }

    // 교수 상세 정보 조회
    @GetMapping("/professors/{email}")
    public ResponseEntity<ProfessorDetailResDto> getProfessorDetail(@PathVariable String email) {
        ProfessorDetailResDto dto = professorService.getProfessorDetailByEmail(email);
        return ResponseEntity.ok(dto);
    }

    // 학생 정보 수정
    @PutMapping("/student/{email}")
    public ResponseEntity<Void> updateStudentProfile(
            @PathVariable String email,
            @RequestBody StudentModifyReqDto dto
    ) {
        studentService.updateStudentProfile(email, dto);
        return ResponseEntity.ok().build();
    }

    // 교수 정보 수정
    @PutMapping("/professor/{email}")
    public ResponseEntity<Void> updateProfessorProfile(
            @PathVariable String email,
            @RequestBody ProfessorModifyReqDto dto
    ) {
        professorService.updateProfessorProfile(email, dto);
        return ResponseEntity.ok().build();
    }
}

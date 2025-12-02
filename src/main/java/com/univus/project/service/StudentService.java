package com.univus.project.service;

import com.univus.project.dto.student.StudentDetailResDto;
import com.univus.project.dto.student.StudentModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.Student;
import com.univus.project.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

public class StudentService {
    private final StudentRepository studentRepository;

    // 학생 정보 조회
    public StudentDetailResDto getStudentDetailByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보가 없습니다. email=" + email));

        // 공통 정보 세팅
        UserResDto userDto = new UserResDto(student
        );

        // 학생 전용 정보 세팅
        StudentDetailResDto dto = new StudentDetailResDto();
        dto.setUser(userDto);
        dto.setStudentNumber(student.getStudentNumber());
        dto.setMajor(student.getMajor());
        dto.setGrade(student.getGrade());

        return dto;
    }

    // 학생 정보 수정 (email 기준)
    public void updateStudentProfile(String email, StudentModifyReqDto dto) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보가 없습니다. email=" + email));

        // null이 아닌 값만 업데이트
        if (dto.getStudentNumber() != null && !dto.getStudentNumber().isBlank()) {
            student.setStudentNumber(dto.getStudentNumber());
        }
        if (dto.getMajor() != null && !dto.getMajor().isBlank()) {
            student.setMajor(dto.getMajor());
        }
        if (dto.getGrade() != null) {
            student.setGrade(dto.getGrade());
        }

        // @Transactional 때문에 따로 save() 안 해도 update 반영됨
        log.info("학생 정보 수정 완료 → email={}", email);
    }
}

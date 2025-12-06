package com.univus.project.service;

import com.univus.project.dto.student.StudentModifyReqDto;
import com.univus.project.dto.student.StudentDetailResDto;
import com.univus.project.entity.Student;
import com.univus.project.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    private StudentRepository studentRepository;
    private StudentService studentService;
    private Student student;

    @BeforeEach
    void setUp() {
        studentRepository = Mockito.mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);

        student = new Student();
        student.setId(1L);
        student.setEmail("test@test.com");
        student.setName("테스트유저");
        student.setStudentNumber("20241234");
        student.setMajor("컴퓨터공학과");
        student.setGrade(3);
    }

    // 학생 조회 성공
    @Test
    void getStudentDetailByEmail_success() {
        when(studentRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(student));

        StudentDetailResDto result = studentService.getStudentDetailByEmail("test@test.com");

        assertNotNull(result);
        assertEquals("20241234", result.getStudentNumber());
        assertEquals("컴퓨터공학과", result.getMajor());
        assertEquals(3, result.getGrade());
    }

    // 학생 조회 실패
    @Test
    void getStudentDetailByEmail_notFound() {
        when(studentRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> studentService.getStudentDetailByEmail("notfound@test.com")
        );

        assertEquals("학생 정보가 없습니다. email=notfound@test.com", ex.getMessage());
    }

    // 학생 정보 수정 성공
    @Test
    void updateStudentProfile_success() {
        when(studentRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(student));

        StudentModifyReqDto dto = new StudentModifyReqDto();
        dto.setStudentNumber("20250001");
        dto.setMajor("소프트웨어공학과");
        dto.setGrade(4);

        studentService.updateStudentProfile("test@test.com", dto);

        assertEquals("20250001", student.getStudentNumber());
        assertEquals("소프트웨어공학과", student.getMajor());
        assertEquals(4, student.getGrade());

        verify(studentRepository, times(1)).findByEmail("test@test.com");
    }

    // 학생 정보 수정 실패 (존재하지 않는 이메일)
    @Test
    void updateStudentProfile_fail() {
        when(studentRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudentProfile("unknown@test.com", new StudentModifyReqDto())
        );
    }
}

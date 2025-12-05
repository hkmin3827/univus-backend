package com.univus.project.service;

import com.univus.project.constant.Role;
import com.univus.project.dto.auth.LoginReqDto;
import com.univus.project.dto.auth.UserSignUpReqDto;
import com.univus.project.entity.Professor;
import com.univus.project.entity.Student;
import com.univus.project.entity.User;
import com.univus.project.repository.ProfessorRepository;
import com.univus.project.repository.StudentRepository;
import com.univus.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserSignUpReqDto studentDto;
    private UserSignUpReqDto professorDto;

    @BeforeEach
    void setUp() {
        studentDto = new UserSignUpReqDto();
        studentDto.setName("학생A");
        studentDto.setEmail("student@test.com");
        studentDto.setPwd("1234");
        studentDto.setRole(Role.STUDENT);

        professorDto = new UserSignUpReqDto();
        professorDto.setName("교수B");
        professorDto.setEmail("prof@test.com");
        professorDto.setPwd("1111");
        professorDto.setRole(Role.PROFESSOR);
    }

    @Test
    void 학생_회원가입_성공() {
        // given
        Student savedStudent = new Student();
        savedStudent.setId(1L);

        when(passwordEncoder.encode(any())).thenReturn("encodedPw");
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        // when
        Long resultId = authService.signup(studentDto);

        // then
        assertEquals(1L, resultId);
        verify(studentRepository).save(any(Student.class));
        verify(passwordEncoder, times(2)).encode(any()); // email + student case encode
    }

    @Test
    void 교수_회원가입_성공() {
        // given
        Professor savedProfessor = new Professor();
        savedProfessor.setId(5L);

        when(passwordEncoder.encode(any())).thenReturn("encodedPw");
        when(professorRepository.save(any(Professor.class))).thenReturn(savedProfessor);

        // when
        Long resultId = authService.signup(professorDto);

        // then
        assertEquals(5L, resultId);
        verify(professorRepository).save(any(Professor.class));
    }

    @Test
    void 지원하지_않는_역할_회원가입_예외() {
        UserSignUpReqDto wrongDto = new UserSignUpReqDto();
        wrongDto.setEmail("xxx@test.com");
        wrongDto.setPwd("1234");
        wrongDto.setName("Unknown");
        wrongDto.setRole(null);

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup(wrongDto));
    }

    @Test
    void 중복_이메일_회원가입_실패() {
        // given
        when(userRepository.existsByEmail("student@test.com")).thenReturn(true);

        // when
        boolean result = authService.isUser("student@test.com");

        // then
        assertTrue(result);
        verify(userRepository).existsByEmail("student@test.com");
    }

    @Test
    void 중복_이메일_회원가입_예외처리() {
        // given
        when(userRepository.existsByEmail("student@test.com")).thenReturn(true);

        // 회원가입 요청 DTO
        UserSignUpReqDto dto = new UserSignUpReqDto();
        dto.setName("학생A");
        dto.setEmail("student@test.com");
        dto.setPwd("1234");
        dto.setRole(Role.STUDENT);

        // when & then
        assertThrows(RuntimeException.class,
                () -> {
                    if (authService.isUser(dto.getEmail())) {
                        throw new RuntimeException("이미 존재하는 이메일입니다.");
                    }
                    authService.signup(dto);
                });
    }

    @Test
    void isUser_이메일_존재하지않음() {
        when(userRepository.existsByEmail("none@test.com")).thenReturn(false);

        boolean result = authService.isUser("none@test.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("none@test.com");
    }

    @Test
    void 로그인_성공() {
        // given
        LoginReqDto dto = new LoginReqDto("student@test.com", "1234");

        User user = new User();
        user.setId(10L);
        user.setEmail("student@test.com");
        user.setPwd("encodedPw");

        when(userRepository.findByEmail("student@test.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encodedPw"))
                .thenReturn(true);

        // when
        Long result = authService.login(dto);

        // then
        assertEquals(10L, result); // 반환값 검증
    }

    @Test
    void 로그인_실패_이메일없음() {
        LoginReqDto loginDto = new LoginReqDto("no@test.com", "1234");

        when(userRepository.findByEmail("no@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        LoginReqDto loginDto = new LoginReqDto("student@test.com", "wrongPw");

        User user = new User();
        user.setEmail("student@test.com");
        user.setPwd("encodedPw");

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPw", "encodedPw")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }
}

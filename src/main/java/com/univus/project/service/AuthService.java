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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

// 회원가입, 로그인
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 여부
    public boolean isUser(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 가입
    public Long signup(UserSignUpReqDto dto) {

        if (dto.getRole() == Role.STUDENT) {

            Student s = new Student();

            // 공통(User) 필드
            s.setEmail(dto.getEmail());
            s.setPwd(passwordEncoder.encode(dto.getPwd()));
            s.setName(dto.getName());
            s.setImage(dto.getImage());
            s.setPhone(dto.getPhone());
            s.setRole(Role.STUDENT);

            // 학생 전용 필드는 회원가입 단계에서 입력받지 않음 → null 저장
            s.setStudentNumber(null);
            s.setMajor(null);
            s.setGrade(null);

            Student saved = studentRepository.save(s);
            return saved.getId();
        }

        else if (dto.getRole() == Role.PROFESSOR) {

            Professor p = new Professor();

            // 공통(User) 필드
            p.setEmail(dto.getEmail());
            p.setPwd(passwordEncoder.encode(dto.getPwd()));
            p.setName(dto.getName());
            p.setImage(dto.getImage());
            p.setPhone(dto.getPhone());
            p.setRole(Role.PROFESSOR);

            // 교수 전용 필드는 회원가입 때 입력받지 않음 → null 저장
            p.setDepartment(null);
            p.setPosition(null);

            Professor saved = professorRepository.save(p);
            return saved.getId();
        }

        throw new IllegalArgumentException("지원하지 않는 역할입니다: " + dto.getRole());
    }

    public Long login(LoginReqDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        // 🔥 raw: dto.getPwd(), encoded: user.getPwd()
        if (!passwordEncoder.matches(dto.getPwd(), user.getPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user.getId();
    }



    private User convertDtoToEntity(UserSignUpReqDto userSignUpReqDto) {
        User user = new User();
        user.setEmail(userSignUpReqDto.getEmail());
        user.setPwd(userSignUpReqDto.getPwd());
        user.setName(userSignUpReqDto.getName());
        user.setImage(userSignUpReqDto.getImage());
        user.setRole(userSignUpReqDto.getRole());
        return user;

    }
}

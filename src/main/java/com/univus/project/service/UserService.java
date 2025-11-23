package com.univus.project.service;

import com.univus.project.dto.student.StudentModifyReqDto;
import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.Student;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// 공통 기능 로직
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 전체 조회
    public List<UserResDto> findAll() {
        List<User> users = userRepository.findAll()
                .stream() // 리스트 데이터를 가공하기 편한 상태로 바꿈
                .filter(User::isActive)   // active == true 만
                .toList(); // 리스트 상태 복구

        List<UserResDto> userResDtos = new ArrayList<>();
        for (User user : users) {
            userResDtos.add(covertEntityToDto(user));
        }
        return userResDtos;
    }

    // 개별 회원 조회
    public UserResDto findByEmail(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        return covertEntityToDto(user);
    }

    @Transactional
    public void updateUserProfile(Long userId, UserModifyReqDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getImage() != null) user.setImage(dto.getImage());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getProfile() != null) user.setProfile(dto.getProfile());
    }

    // 학생
//    @Transactional
//    public void updateStudentProfile(Long userId, StudentModifyReqDto dto) {
//        Student student = studentRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학생입니다.");
//
//        if (dto.getMajor() != null) student.setMajor(dto.getMajor());
//        if (dto.getStudentNumber() != null) student.setStudentNumber(dto.getStudentNumber());
//        if (dto.getGrade() != null) student.setGrade(dto.getGrade());
//    }
//
//    // 교수
//    @Transactional
//    public void updateProfessorProfile(Long userId, ProfessorProfileModifyReqDto dto) {
//        Professor professor = professorRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교수입니다.");
//
//        if (dto.getOffice() != null) professor.setOffice(dto.getOffice());
//        if (dto.getPosition() != null) professor.setPosition(dto.getPosition());
//        if (dto.getResearchField() != null) professor.setResearchField(dto.getResearchField());
//    }


    // 회원 스스로 탈퇴
    public boolean withdrawUser(String email) {
        try {
            User user = userRepository.findByEmailAndActiveTrue(email)
                    .orElseThrow(() -> new RuntimeException("이미 탈퇴했거나 존재하지 않는 회원입니다."));

            user.setActive(false);   // true → false 로 변경 (탈퇴 처리)

            userRepository.save(user);   // @Transactional이면 생략해도 변경감지로 반영됨
            return true;
        } catch (Exception e) {
            log.error("회원 탈퇴 실패 : {}", e.getMessage());
            return false;
        }
    }

    // 관리자에 의한 강제 삭제
    public boolean deleteUserByAdmin(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

            userRepository.delete(user);   // 실제 DB에서 행 삭제
            return true;
        } catch (Exception e) {
            log.error("관리자 회원 삭제 실패 : {}", e.getMessage());
            return false;
        }
    }

    private UserResDto covertEntityToDto(User user) {
        UserResDto userResDto = new UserResDto();
        userResDto.setEmail(user.getEmail());
        userResDto.setName(user.getName());
        userResDto.setImage(user.getImage());
        userResDto.setRegDate(user.getRegDate());
        return userResDto;
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
    }

}

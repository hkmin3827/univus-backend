package com.univus.project.dto.user;

import com.univus.project.constant.Role;
import com.univus.project.entity.Professor;
import com.univus.project.entity.Student;
import com.univus.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResDto {

    // 공통(User) 정보
    private String email;
    private String name;
    private String image;
    private String phone;
    private String profile;        // JSON 문자열
    private LocalDateTime regDate; // 가입일 (User의 createdAt 또는 별도 필드)
    private Role role;

    // 학생 전용
    private String studentNumber;  // 학번
    private String major;          // 전공
    private Integer grade;         // 학년

    // 교수 전용
    private String department;     // 학과
    private String position;       // 직위

    // === 정적 팩토리 메서드들 ===

    // 공통 User만 있는 경우 (혹시 ADMIN 같은 거 쓸 때)
    public static LoginResDto fromUser(User user) {
        LoginResDto dto = new LoginResDto();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setImage(user.getImage());
        dto.setPhone(user.getPhone());
        dto.setProfile(user.getProfile());
        dto.setRole(user.getRole());
        // regDate는 User 엔티티에 필드가 뭐로 돼 있는지에 맞춰서 넣어주면 됨
        // 예: dto.setRegDate(user.getCreatedAt());
        return dto;
    }

    public static LoginResDto fromStudent(Student student) {
        LoginResDto dto = fromUser(student); // User 공통 필드 채우기

        dto.setStudentNumber(student.getStudentNumber());
        dto.setMajor(student.getMajor());
        dto.setGrade(student.getGrade());
        // Student.createdAt을 regDate로 쓰고 싶으면 여기에서
        dto.setRegDate(student.getCreatedAt());

        return dto;
    }

    public static LoginResDto fromProfessor(Professor professor) {
        LoginResDto dto = fromUser(professor); // User 공통 필드 채우기

        dto.setDepartment(professor.getDepartment());
        dto.setPosition(professor.getPosition());
        dto.setRegDate(professor.getCreatedAt());

        return dto;
    }
}

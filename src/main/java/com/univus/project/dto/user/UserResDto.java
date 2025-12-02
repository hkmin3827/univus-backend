package com.univus.project.dto.user;

import com.univus.project.constant.Role;
import com.univus.project.entity.Professor;
import com.univus.project.entity.Student;
import com.univus.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String image;
    private LocalDateTime regDate;
    private Role role;
    private boolean active;

    private String department;
    private String position;
    private String studentNumber;
    private String major;
    private Integer grade;

    public UserResDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.image = user.getImage();
        this.phone = user.getPhone();
        this.regDate = user.getRegDate();
        this.role = user.getRole();
        this.active = user.isActive();

        if (user instanceof Professor) {
            Professor professor = (Professor) user;
            this.department = professor.getDepartment();
            this.position = professor.getPosition();
        }

        if (user instanceof Student) {
            Student student = (Student) user;
            this.studentNumber = student.getStudentNumber();
            this.major = student.getMajor();
            this.grade = student.getGrade();
        }
    }

}


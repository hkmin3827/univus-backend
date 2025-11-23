package com.univus.project.entity;
// 회원

import com.univus.project.constant.Role;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
//@Table(name= "user")
@Inheritance(strategy = InheritanceType.JOINED)   // 하위 엔티티 분리 테이블 방식 (교수 / 학생)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(length = 30)
    private String phone;

    @Lob
    @Column(columnDefinition = "JSON")
    private String profile;

    private LocalDateTime regDate;
    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;        // 권한 (STUDENT, PROFESSOR 등)


    @Column(nullable = false)
    private boolean active = true; // 회원 가입시 (활성)
}
package com.univus.project.entity;
// User 상속 받은 하위 교수 엔티티

import com.univus.project.constant.Role;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "professor")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Professor extends User {
    // 부가적으로 넣고 싶은 교수 정보 => 소속 학과 정보 or 연구실 번호 등 변수 생성 가능

    @Column(length = 50)
    private String department;          // 소속 학과

    @Column(length = 30)
    private String position;        // 직위 (조교, 부교수, 교수 등)

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간 updatable = false(변경 불가)

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 마지막 수정 시간

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 삭제된 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        //  기본 권한이 없으면 교수
        if (getRole() == null) {
            setRole(Role.PROFESSOR);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
package com.univus.project.entity;
// User 상속 받은 하위 학생 엔티티

import com.univus.project.constant.Role;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="student")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor

public class Student extends User {
    @Column(nullable = false, length = 20)
    private String studentNumber;   // 학번

    @Column(length = 50)
    private String major;           // 전공

    @Column
    private Integer grade;          // 학년 (1,2,3,4)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시간 updatable = false(변경 불가)

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 마지막 수정 시간

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 삭제된 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

package com.univus.project.entity;

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

    @Column(length = 50)
    private String department;

    @Column(length = 30)
    private String position;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (getRole() == null) {
            setRole(Role.PROFESSOR);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
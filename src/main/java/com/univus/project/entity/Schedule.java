// com.univus.project.entity.Schedule.java
package com.univus.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 512)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTime; // 일정 날짜 (시간까지 필요하면 LocalDateTime으로 변경)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;   // 로그인한 사용자
}

package com.univus.project.entity;
// 출석 관리

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @ToString
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;          // 출석한 사용자

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;        // 출석한 게시판

    private LocalDate date;     // 출석 날짜

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
    }
}

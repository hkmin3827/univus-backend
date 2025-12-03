package com.univus.project.entity;
// 활동 로그
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
@Entity

public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;                  // 사용자 식별

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;                // 프로젝트 활동 식별

    // 집계 항목
    private int postCount;              // 작성한 게시글 수
    private int commentCount;           // 작성한 댓글 수
    private int reactionCount;          // 내가 누른 Reaction 수

    private int todoCompleted;          // 완료한 TodoList 수
    private int todoUncompleted;        // 미완료 TodoList 수

    private int attendanceTotal;        // 총 출석일
    private int attendanceStreak;       // 연속 출석일
    private int attendanceThisMonth;    // 이번 달 출석일

    private int contributionScore;      // 기여도 점수

    private LocalDateTime lastUpdated;  // 활동로그 업데이트 날짜
}

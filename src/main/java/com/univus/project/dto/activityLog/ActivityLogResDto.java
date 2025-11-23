package com.univus.project.dto.activityLog;

import com.univus.project.entity.ActivityLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class ActivityLogResDto {
    // User 필드
    private Long userId;
    private String userName;
    private String userEmail;

    // Board 필드
    private Long boardId;
    private String boardName;

    // 활동 기여도 필드 - 게시글, 댓글, 공감
    private int postCount;
    private int commentCount;
    private int reactionCount;

    // TodoList 필드 - 완료, 미완료
    private int todoCompleted;
    private int todoUncompleted;

    // 출석 필드
    private int attendanceTotal;
    private int attendanceStreak;
    private int attendanceThisMonth;

    private LocalDateTime lastUpdated;

    public ActivityLogResDto(ActivityLog activityLog) {
        this.userId = activityLog.getUser().getId();
        this.userName = activityLog.getUser().getName();
        this.userEmail = activityLog.getUser().getEmail();

        this.boardId = activityLog.getBoard().getId();
        this.boardName = activityLog.getBoard().getName();

        this.postCount = activityLog.getPostCount();
        this.commentCount = activityLog.getCommentCount();
        this.reactionCount = activityLog.getReactionCount();

        this.todoCompleted = activityLog.getTodoCompleted();
        this.todoUncompleted = activityLog.getTodoUncompleted();

        this.attendanceTotal = activityLog.getAttendanceTotal();
        this.attendanceStreak = activityLog.getAttendanceStreak();
        this.attendanceThisMonth = activityLog.getAttendanceThisMonth();

        this.lastUpdated = activityLog.getLastUpdated();
    }
}

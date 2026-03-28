 package com.univus.project.dto.activityLog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserContributionDetailDto {

    private Long userId;
    private String userName;
    private String userImage;

    private int postCount;
    private int commentCount;
    private int reactionCount;
    private int todoCompleted;
    private int todoUncompleted;

    private int attendanceTotal;
    private int attendanceStreak;
    private int attendanceThisMonth;

    private int contributionScore;
}

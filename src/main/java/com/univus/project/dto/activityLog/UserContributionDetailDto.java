 package com.univus.project.dto.activityLog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserContributionDetailDto {

    // 특정 팀원 상세 인사이트(클릭시 이동)

    private Long userId;
    private String userName;
    private String userImage;

    // 카테고리별 카운트
    private int postCount;
    private int commentCount;
    private int reactionCount;  //내가 누른 공감
    private int todoCompleted;
    private int todoUncompleted;

    // 출석 관련
    private int attendanceTotal;
    private int attendanceStreak;
    private int attendanceThisMonth;

    // 총 기여도 점수
    private int contributionScore;
}

package com.univus.project.dto.activityLog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardUserContributionDto {
    
    // 전체 팀원 기여도
    
    private Long userId;
    private String userName;
    private String userImage;   // 프로필 이미지 있으면
    private int contributionScore;
}

package com.univus.project.dto.activityLog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardUserContributionDto {
    
    private Long userId;
    private String userName;
    private String userImage;
    private int contributionScore;
}

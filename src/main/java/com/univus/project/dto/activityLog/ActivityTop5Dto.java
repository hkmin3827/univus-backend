package com.univus.project.dto.activityLog;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityTop5Dto {
    private Long userId;
    private String userName;
    private String userImage;
    private Long count;
}

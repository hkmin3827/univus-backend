package com.univus.project.dto.activityLog;

import com.univus.project.constant.Role;
import com.univus.project.entity.User;
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
    private Role role;   // 🔥 새로 추가

    // 🔥 JPQL에서 호출하는 생성자 (User, Long)
    public ActivityTop5Dto(User user, Long count) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.userImage = user.getImage();
        this.count = count;
        this.role = user.getRole();   // 여기서 role 세팅(STUDENT / PROFESSOR)
    }
}

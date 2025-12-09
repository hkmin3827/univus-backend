package com.univus.project.entity;

import com.univus.project.constant.NotificationType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // 알림 받을 유저
    private Long teamId;
    private Long boardId;
    private Long postId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;  // COMMENT, TODO_DONE 등

    private String message;

    private boolean checked;       // 이미 확인했는지 여부

    private LocalDateTime createdAt;
}

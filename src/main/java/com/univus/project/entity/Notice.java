package com.univus.project.entity;
// 공지사항
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "notice_id")
    private Long id;                    // 공지 Id

    @Column(nullable = false, length = 256)
    private String title;               // 공지 제목

    @Lob
    @Column(nullable = false)
    private  String content;            // 공지 내용

    // 작성자 정보 -> User 연동
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                  // 작성자 (교수 권한 부여 설정)

    private LocalDateTime createTime;   // 생성 시간

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

    public String getWriterName() {
        return this.user != null ? this.user.getName() : null;
    }

    public String getWriterEmail() {
        return this.user != null ? this.user.getEmail() : null;
    }
}

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
    private Long id;                    // Id
    private String email;               // 작성자 이메일

    @Column(nullable = false, length = 256)
    private String title;               // 공지 제목

    @Lob
    private  String content;            // 공지 내용

    private String name;                // 작성자

    private LocalDateTime createTime;   // 생성 시간

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
}

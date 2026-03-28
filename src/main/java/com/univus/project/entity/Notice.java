package com.univus.project.entity;

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
    private Long id;

    @Column(nullable = false, length = 256)
    private String title;

    @Lob
    @Column(nullable = false)
    private  String content;

    @Lob
    private String fileUrl;
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
}

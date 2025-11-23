package com.univus.project.entity;
// 공감

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter @Setter @NoArgsConstructor @ToString
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_user_post", columnNames = {"user_id", "post_id"})
        }   // 한 user가 같은 게시글에 중복 공감 방지
)

public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private Long id;

    // 작성자 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 게시글 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}

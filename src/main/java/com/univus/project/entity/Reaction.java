package com.univus.project.entity;

import com.univus.project.constant.ReactionType;
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
        }
)

public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}

package com.univus.project.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="todo_id")
    private Long id;

    @Column(nullable = false, length = 256)
    private String content;
    private boolean done = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}

package com.univus.project.entity;
// Todolist
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
    private Long id;                    // Id
    private String email;               // 작성자 이메일

    @Column(nullable = false, length = 256)
    private String content;             // TodoList 내용

    private String name;                // 작성자 이름

    private boolean done = false;       // TodoList 완료 여부

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;                // todolist가 속한 프로젝트 식별

    private LocalDateTime createTime;   // 생성 시간

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}

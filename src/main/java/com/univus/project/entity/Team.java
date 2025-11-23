package com.univus.project.entity;

import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;    // 팀 이름
    private String description; // 팀 소개

    // 팀장 userId
    private String leader;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<Board> boards = new ArrayList<>();
}
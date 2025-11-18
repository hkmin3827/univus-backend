package com.univus.project.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResDto {
    private String email;
    private String name;
    private String image;
    private LocalDateTime regDate;

}

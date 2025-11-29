package com.univus.project.dto.user;

import com.univus.project.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String image;
    private LocalDateTime regDate;
    private Role role;

    public UserResDto(Long id, String email, String name, Role role, String image, LocalDateTime regDate) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.regDate = regDate;
        this.role = role;
    }

}


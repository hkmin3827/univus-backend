package com.univus.project.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetConfirmDto {
    private String token;
    private String newPassword;
}

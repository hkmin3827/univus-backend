package com.univus.project.dto.team;

import com.univus.project.constant.Role;
import com.univus.project.entity.TeamMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeamMemberResDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Role userRole;
    private String userImage;

    public TeamMemberResDto(TeamMember teamMember) {
        this.userId = teamMember.getUser().getId();
        this.userName = teamMember.getUser().getName();
        this.userEmail = teamMember.getUser().getEmail();
        this.userRole = teamMember.getUser().getRole();
        this.userImage = teamMember.getUser().getImage();
    }
}

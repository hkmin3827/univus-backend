package com.univus.project.dto.team;

import com.univus.project.constant.Role;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Member;
import java.util.List;
import java.util.stream.Collectors;
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

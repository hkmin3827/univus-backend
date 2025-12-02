package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.team.TeamMemberResDto;
import com.univus.project.entity.User;
import com.univus.project.service.TeamMemberService;
import com.univus.project.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    @GetMapping("/{teamId}/members")
    public List<TeamMemberResDto> getTeamMembers(@PathVariable Long teamId) {
        return teamMemberService.getTeamMembers(teamId);
    }

    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<Boolean> leaveTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok((teamMemberService.leaveTeam(teamId, user)));
    }

    @DeleteMapping("/{teamId}/kick")
    public ResponseEntity<Boolean> kickMember(
            @PathVariable Long teamId,
            @RequestParam Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(teamMemberService.kickMember(teamId, user.getId(), targetUserId));
    }
}

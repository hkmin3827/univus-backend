package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.team.TeamCreateReqDto;
import com.univus.project.dto.team.TeamInviteResDto;
import com.univus.project.dto.team.TeamResDto;
import com.univus.project.entity.User;
import com.univus.project.service.TeamInviteService;
import com.univus.project.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TeamService teamService;
    private final TeamInviteService teamInviteService;

    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @PostMapping
    public ResponseEntity<TeamResDto> createTeam(@RequestBody TeamCreateReqDto dto) {
        User leader = getCurrentUser();
        TeamResDto res = teamService.createTeam(dto, leader);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResDto> getTeam(@PathVariable Long teamId,  @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = getCurrentUser();
        TeamResDto res = teamService.getTeam(teamId,user.getId());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Boolean> deleteTeam(
            @PathVariable Long teamId
    ) {
        User user = getCurrentUser();
        teamService.deleteTeam(teamId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify/{teamId}")
    public ResponseEntity<Long> updateTeam(@PathVariable Long teamId, @RequestBody TeamCreateReqDto dto, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = getCurrentUser();
        teamService.updateTeam(teamId, dto, user);
        return ResponseEntity.ok(teamId);
    }

    @PostMapping("/{teamId}/invites")
    public ResponseEntity<TeamInviteResDto> createInvite(@PathVariable Long teamId) {
        User inviter = getCurrentUser();
        TeamInviteResDto res = teamInviteService.createInvite(teamId, inviter, frontendBaseUrl);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/invites/{token}")
    public ResponseEntity<TeamInviteResDto> getInviteInfo(@PathVariable String token) {
        TeamInviteResDto res = teamInviteService.getInviteInfo(token, frontendBaseUrl);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<Void> acceptInvite(@PathVariable String token) {
        User user = getCurrentUser();
        teamInviteService.acceptInvite(token, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<TeamResDto>> getMyTeams() {
        User loginUser = getCurrentUser();
        List<TeamResDto> teams = teamService.getTeamsByUser(loginUser);
        return ResponseEntity.ok(teams);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인 사용자를 찾을 수 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}

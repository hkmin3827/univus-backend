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

// 팀 관련 API 모음 (팀 생성, 조회, 초대 등)
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TeamService teamService;
    private final TeamInviteService teamInviteService;

    // 프론트엔드 기본 주소 (application.yml / properties 에서 설정)
    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    /**
     * 팀 생성
     * - Body: TeamCreateReqDto (팀 이름, 소개)
     * - 로그인한 유저를 팀장으로 설정
     */
    @PostMapping
    public ResponseEntity<TeamResDto> createTeam(@RequestBody TeamCreateReqDto dto) {
        User leader = getCurrentUser();
        TeamResDto res = teamService.createTeam(dto, leader);
        return ResponseEntity.ok(res);
    }

    /**
     * 팀 상세 조회
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResDto> getTeam(@PathVariable Long teamId) {
        TeamResDto res = teamService.getTeam(teamId);
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
    public ResponseEntity<Long> updateTeam(@PathVariable Long teamId, TeamCreateReqDto dto, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = getCurrentUser();
        teamService.updateTeam(teamId, dto, user);
        return ResponseEntity.ok(teamId);
    }
    /**
     * 팀 초대 URL 생성 (팀장만 가능)
     * - 프론트에서 이 URL 을 복사해서 공유
     */
    @PostMapping("/{teamId}/invites")
    public ResponseEntity<TeamInviteResDto> createInvite(@PathVariable Long teamId) {
        User inviter = getCurrentUser();
        TeamInviteResDto res = teamInviteService.createInvite(teamId, inviter, frontendBaseUrl);
        return ResponseEntity.ok(res);
    }

    /**
     * 초대 정보 조회
     * - 초대 페이지 진입 시 토큰으로 팀/초대한 사람/만료 여부 확인
     */
    @GetMapping("/invites/{token}")
    public ResponseEntity<TeamInviteResDto> getInviteInfo(@PathVariable String token) {
        TeamInviteResDto res = teamInviteService.getInviteInfo(token, frontendBaseUrl);
        return ResponseEntity.ok(res);
    }

    /**
     * 초대 수락 = 팀 가입
     * - 로그인한 유저를 해당 팀 멤버로 추가
     */
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
    /**
     * 현재 로그인한 User 엔티티 가져오기
     * - 프로젝트에서 사용하는 CustomUserDetails 기준
     * - 필요하면 이 부분은 각자 프로젝트 상황에 맞게 수정
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인 사용자를 찾을 수 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();  // CustomUserDetails 안의 User 반환
    }
}

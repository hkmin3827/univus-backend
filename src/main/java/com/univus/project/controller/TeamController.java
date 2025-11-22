package com.univus.project.controller;

import com.univus.project.dto.team.*;
import com.univus.project.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    // 팀 생성
    @PostMapping
    public ResponseEntity<Boolean> createTeam(@RequestBody TeamCreateReqDto dto) {
        return ResponseEntity.ok(teamService.createTeam(dto));
    }

    // 팀 전체 조회
    @GetMapping
    public ResponseEntity<List<TeamResDto>> getTeams() {
        return ResponseEntity.ok(teamService.findAll());
    }

    // 개별 팀 조회
    @GetMapping("/{teamName}")
    public ResponseEntity<TeamResDto> getTeam(@PathVariable String teamName) {
        return ResponseEntity.ok(teamService.findTeam(teamName));
    }

    // 팀 수정
    @PutMapping("/{teamName}")
    public ResponseEntity<Boolean> updateTeam(
            @PathVariable String teamName,
            @RequestBody TeamModifyReqDto dto
    ) {
        dto.setTeamName(teamName);
        return ResponseEntity.ok(teamService.modifyTeam(dto));
    }

    // 팀 삭제
    @DeleteMapping("/{teamName}")
    public ResponseEntity<Boolean> deleteTeam(@PathVariable String teamName) {
        return ResponseEntity.ok(teamService.deleteTeam(teamName));
    }

    // 팀 초대
    @PostMapping("/{teamName}/invite")
    public ResponseEntity<Boolean> inviteMember(
            @PathVariable String teamName,
            @RequestBody TeamInviteReqDto dto
    ) {
        dto.setTeamName(teamName);
        return ResponseEntity.ok(teamService.inviteMember(dto));
    }

    // 초대 수락
    @PostMapping("/invite/{inviteId}/accept")
    public ResponseEntity<Boolean> acceptInvite(@PathVariable Long inviteId) {
        return ResponseEntity.ok(teamService.acceptInvite(inviteId));
    }

    // 초대 거절
    @PostMapping("/invite/{inviteId}/decline")
    public ResponseEntity<Boolean> declineInvite(@PathVariable Long inviteId) {
        return ResponseEntity.ok(teamService.declineInvite(inviteId));
    }
}

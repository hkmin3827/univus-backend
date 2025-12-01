package com.univus.project.controller;

import com.univus.project.dto.team.TeamMemberResDto;
import com.univus.project.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}

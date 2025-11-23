package com.univus.project.repository;

import com.univus.project.entity.Team;
import com.univus.project.entity.TeamInvite;
import com.univus.project.constant.InviteStatus;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    List<TeamInvite> findByInvitee_EmailAndStatus(String email, InviteStatus status);

    boolean existsByInviteeAndTeamAndStatus(User invitee, Team team, InviteStatus status);

    List<TeamInvite> findByTeam(Team team);
}

package com.univus.project.repository;

import com.univus.project.entity.TeamInvite;
import com.univus.project.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    List<TeamInvite> findByInviteeAndStatus(String invitee, InviteStatus status);
}

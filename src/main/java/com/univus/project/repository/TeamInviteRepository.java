package com.univus.project.repository;

import com.univus.project.entity.TeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    Optional<TeamInvite> findByInviteToken(String inviteToken);
}

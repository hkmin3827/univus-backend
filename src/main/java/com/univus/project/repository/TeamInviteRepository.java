package com.univus.project.repository;

import com.univus.project.entity.TeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// TeamInvite 엔티티용 레포지토리
public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {

    // 초대 토큰으로 초대 내역 찾기
    Optional<TeamInvite> findByInviteToken(String inviteToken);
}

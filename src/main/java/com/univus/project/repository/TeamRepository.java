package com.univus.project.repository;

import com.univus.project.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

// Team 엔티티용 기본 CRUD 레포지토리
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByTeamName(String teamName);
}

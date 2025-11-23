package com.univus.project.repository;

import com.univus.project.entity.TeamMember;
import com.univus.project.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    void deleteByTeam(Team team);
}

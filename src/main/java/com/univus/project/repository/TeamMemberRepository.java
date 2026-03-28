package com.univus.project.repository;

import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamAndUser(Team team, User user);

    long countByTeam(Team team);
    List<TeamMember> findByUser(User user);

    List<TeamMember> findByTeamId(Long teamId);
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.user WHERE tm.team.id = :teamId")
    List<TeamMember> findByTeamIdWithUser(Long teamId);
    boolean existsByTeamId(Long teamId);
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    void deleteByTeamId(Long teamId);
}

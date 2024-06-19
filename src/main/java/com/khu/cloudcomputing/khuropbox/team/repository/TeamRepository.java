package com.khu.cloudcomputing.khuropbox.team.repository;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import com.khu.cloudcomputing.khuropbox.team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<Team> findByTeamId(Integer id);
    void deleteById(Integer id);
}

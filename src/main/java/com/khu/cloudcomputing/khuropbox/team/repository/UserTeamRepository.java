package com.khu.cloudcomputing.khuropbox.team.repository;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamRoleMapping;
import com.khu.cloudcomputing.khuropbox.team.dto.UserRoleMapping;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.entity.RoleMapping;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import com.khu.cloudcomputing.khuropbox.team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Integer> {
    @Query("select u.team as team, u.role as role from UserTeam u left outer join Team t on u.team.teamId=t.teamId " +
            "left outer join UserEntity e on u.user.username=e.username where u.user.username=:userName")
    List<TeamRoleMapping> findMyTeam(@Param(value="userName")String userName);
    @Query("select u.user as user, u.role as role from UserTeam u left outer join Team t on u.team.teamId=t.teamId " +
            "left outer join UserEntity e on u.user.username=e.username where u.team.teamId=:teamId")
    List<UserRoleMapping> findTeamMember(@Param(value="teamId") Integer teamId);
    RoleMapping findByUser_IdAndTeam_teamId(String userId, Integer teamId);
    @Modifying
    @Query("update UserTeam u set u.role=:role where u.user.username=:userName and u.team.teamId=:teamId")
    void updateRole(@Param(value="teamId")Integer teamId, @Param(value="userName")String userName, @Param(value="role")Role role);
    @Modifying
    @Query("delete from UserTeam u where u.team.teamId=:teamId and u.user.id=:userId")
    void deleteByIndex(@Param(value="teamId")Integer teamId, @Param(value="userId")String userId);
    @Modifying
    @Query("delete from UserTeam u where u.team.teamId=:teamId and u.user.username=:userName")
    void deleteByName(@Param(value="teamId")Integer teamId, @Param(value="userName")String userName);
    @Modifying
    @Query("delete from UserTeam u where u.team.teamId=:teamId")
    void deleteByTeamId(@Param(value="teamId")Integer teamId);
    boolean existsByUserAndTeam(UserEntity user, Team team);
}

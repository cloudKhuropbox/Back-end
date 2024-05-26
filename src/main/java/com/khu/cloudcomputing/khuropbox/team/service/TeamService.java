package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.dto.InsertTeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamRoleDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.UserRoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TeamService {
    List<TeamRoleDTO> findMyTeam(String userName);
    List<UserRoleDTO> findTeamMember(Integer teamId);
    Integer joinTeam(InsertTeamDTO info);
    Integer createTeam(TeamDTO teamDTO, UserEntity user);
    String findUserRole(String userId, Integer teamId);
    void updateRole(Integer teamId, String userName, String role);
    void deleteByIndex(Integer teamId, String userId);
    void deleteByName(Integer teamId, String userName);
    void deleteByTeamId(Integer teamId);
}
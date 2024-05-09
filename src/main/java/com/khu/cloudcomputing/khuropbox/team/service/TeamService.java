package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.dto.InsertTeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TeamService {
    List<TeamDTO> findMyTeam(String userName);
    List<UserEntity> findTeamMember(Integer teamId);
    Integer joinTeam(InsertTeamDTO info);
    Integer createTeam(TeamDTO teamDTO, UserEntity user);
    UserEntity findTeamAdmin(Integer teamId);
    void deleteByIndex(Integer teamId, String userId);
    void deleteByName(Integer teamId, String userName);
    void deleteByTeamId(Integer teamId);
}
package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.team.dto.*;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import com.khu.cloudcomputing.khuropbox.team.repository.TeamRepository;
import com.khu.cloudcomputing.khuropbox.team.repository.UserTeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {
    private final UserTeamRepository userTeamRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    @Override
    public List<TeamRoleDTO> findMyTeam(String userName){
        List<TeamRoleMapping> teamRoleMapping=userTeamRepository.findMyTeam(userName);
        List<TeamRoleDTO> teamRole=new ArrayList<>();
        for (TeamRoleMapping team:teamRoleMapping) {
            TeamRoleDTO teamRoleDTO=new TeamRoleDTO(team);
            teamRole.add(teamRoleDTO);
        }
        return teamRole;
    }
    @Override
    public List<UserRoleDTO> findTeamMember(Integer teamId){
        List<UserRoleMapping> userRoleMapping=userTeamRepository.findTeamMember(teamId);
        List<UserRoleDTO> userRole=new ArrayList<>();
        for (UserRoleMapping user:userRoleMapping) {
            UserRoleDTO userRoleDTO=new UserRoleDTO(user);
            userRole.add(userRoleDTO);
        }
        return userRole;
    }
    @Override
    public Integer joinTeam(InsertTeamDTO info){
        Team team=teamRepository.findById(info.getTeam()).orElseThrow();
        UserEntity user=userRepository.findByUsername(info.getUserName());
        if(user==null){
            return -1;
        }
        UserTeamDTO userTeam=new UserTeamDTO();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRole(info.getRole());
        return userTeamRepository.save(userTeam.toEntity()).getUserTeamId();
    }
    @Override
    public Integer createTeam(TeamDTO teamDTO, UserEntity user){
        Integer returnValue = teamRepository.save(teamDTO.toEntity()).getTeamId();
        UserTeamDTO userTeam=new UserTeamDTO();
        userTeam.setUser(user);
        userTeam.setTeam(teamRepository.findByTeamId(returnValue).orElseThrow());
        userTeam.setRole("owner");
        userTeamRepository.save(userTeam.toEntity());
        return returnValue;
    }
    @Override
    public String findUserRole(String userId, Integer teamId){
        return userTeamRepository.findByUser_IdAndTeam_teamId(userId, teamId).getRole();
    }
    @Override
    public void updateRole(Integer teamId, String userName, String role){
        if(role.equals("admin") || role.equals("customer"))
            userTeamRepository.updateRole(teamId, userName, role);
    }
    @Override
    public void deleteByIndex(Integer teamId, String userId){
        userTeamRepository.deleteByIndex(teamId, userId);
    }
    @Override
    public void deleteByName(Integer teamId, String userName){
        userTeamRepository.deleteByName(teamId, userName);
    }
    @Override
    public void deleteByTeamId(Integer teamId){
        userTeamRepository.deleteByTeamId(teamId);
        teamRepository.deleteById(teamId);
    }
}

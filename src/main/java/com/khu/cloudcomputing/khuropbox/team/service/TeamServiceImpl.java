package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.team.dto.InsertTeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.UserTeamDTO;
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
    public List<TeamDTO> findMyTeam(String userName){
        List<Team> list = userTeamRepository.findByUserName(userName);
        List<TeamDTO> listDTO = new ArrayList<>();
        for (Team team : list) {
            listDTO.add(new TeamDTO(team));
        }
        return listDTO;
    }
    @Override
    public List<UserEntity> findTeamMember(Integer teamId){
        return userTeamRepository.findTeamMember(teamId);
    }
    @Override
    public Integer joinTeam(InsertTeamDTO info){
        Team team=teamRepository.findById(info.getTeam()).orElseThrow();
        UserEntity user=userRepository.findByUsername(info.getUserName());
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
        userTeam.setRole("admin");
        userTeamRepository.save(userTeam.toEntity());
        return returnValue;
    }
    @Override
    public UserEntity findTeamAdmin(Integer teamId){
        return userTeamRepository.findTeamAdmin(teamId);
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

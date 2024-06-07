package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.team.dto.*;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import com.khu.cloudcomputing.khuropbox.team.repository.TeamRepository;
import com.khu.cloudcomputing.khuropbox.team.repository.UserTeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    /**
     * 사용자의 팀 목록을 조회합니다.
     *
     * @param userName 사용자 이름
     * @return 팀 정보 DTO 목록
     */
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

    /**
     * 팀의 멤버 목록을 조회합니다.
     *
     * @param teamId 팀 ID
     * @return 사용자 엔티티 목록
     */
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

    /**
     * 사용자가 팀에 가입합니다.
     *
     * @param info 팀 가입 정보 DTO
     * @return 생성된 사용자 팀 ID
     * @throws GeneralException 팀 또는 사용자를 찾을 수 없는 경우, 이미 팀에 가입된 경우
     */
    @Override
    public ResponseEntity<?> joinTeam(InsertTeamDTO info) {
        Team team = teamRepository.findById(info.getTeam())
                .orElseThrow(() -> new GeneralException(ErrorStatus._TEAM_NOT_FOUND.getCode(), "Team not found", ErrorStatus._TEAM_NOT_FOUND.getHttpStatus()));
        UserEntity user = userRepository.findByUsername(info.getUserName());
        if(user==null){
            throw new GeneralException(ErrorStatus._USER_NOT_FOUND.getCode(), "User not found", ErrorStatus._USER_ALREADY_IN_TEAM.getHttpStatus());
        }
        if (userTeamRepository.existsByUserAndTeam(user, team)) {
            throw new GeneralException(ErrorStatus._USER_ALREADY_IN_TEAM.getCode(), "User already in team", ErrorStatus._USER_ALREADY_IN_TEAM.getHttpStatus());
        }

        UserTeamDTO userTeam = new UserTeamDTO();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRole(info.getRole());
        userTeamRepository.save(userTeam.toEntity());
        return new ResponseEntity<>(SuccessStatus._TEAM_JOINED.getMessage(), SuccessStatus._TEAM_JOINED.getHttpStatus());
    }

    /**
     * 새로운 팀을 생성합니다.
     *
     * @param teamDTO 팀 정보 DTO
     * @param user 사용자 엔티티
     * @return 생성된 팀 ID
     */
    @Override
    public ResponseEntity<?> createTeam(TeamDTO teamDTO, UserEntity user) {
        Integer teamId = teamRepository.save(teamDTO.toEntity()).getTeamId();
        UserTeamDTO userTeam = new UserTeamDTO();
        userTeam.setUser(user);
        userTeam.setTeam(teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TEAM_NOT_FOUND.getCode(), "Team not found", ErrorStatus._TEAM_NOT_FOUND.getHttpStatus())));
        userTeam.setRole(Role.owner);
        userTeamRepository.save(userTeam.toEntity());
        return new ResponseEntity<>(teamId, SuccessStatus._TEAM_CREATED.getHttpStatus());
    }

    /**
     * 팀의 관리자를 조회합니다.
     *
     * @param teamId 팀 ID
     * @return 사용자 엔티티
     */
    @Override
    public Role findUserRole(String userId, Integer teamId){
        return userTeamRepository.findByUser_IdAndTeam_teamId(userId, teamId).getRole();
    }
    @Override
    public void updateRole(Integer teamId, String userName, Role role){
        if(role.equals(Role.admin) || role.equals(Role.owner))
            userTeamRepository.updateRole(teamId, userName, role);
    }
    /**
     * 팀에서 특정 사용자를 삭제합니다.
     *
     * @param teamId 팀 ID
     * @param userId 사용자 ID
     * @return 삭제 성공 메시지와 상태 코드
     */
    @Override
    public ResponseEntity<?> deleteByIndex(Integer teamId, String userId) {
        userTeamRepository.deleteByIndex(teamId, userId);
        return new ResponseEntity<>(SuccessStatus._TEAM_MEMBER_DELETED.getMessage(), SuccessStatus._TEAM_MEMBER_DELETED.getHttpStatus());
    }

    /**
     * 팀에서 특정 사용자를 삭제합니다 (사용자 이름 기반).
     *
     * @param teamId 팀 ID
     * @param userName 사용자 이름
     * @return 삭제 성공 메시지와 상태 코드
     */
    @Override
    public ResponseEntity<?> deleteByName(Integer teamId, String userName) {
        userTeamRepository.deleteByName(teamId, userName);
        return new ResponseEntity<>(SuccessStatus._TEAM_MEMBER_DELETED.getMessage(), SuccessStatus._TEAM_MEMBER_DELETED.getHttpStatus());
    }

    /**
     * 팀을 삭제합니다.
     *
     * @param teamId 팀 ID
     * @return 삭제 성공 메시지와 상태 코드
     */
    @Override
    public ResponseEntity<?> deleteByTeamId(Integer teamId) {
        userTeamRepository.deleteByTeamId(teamId);
        teamRepository.deleteById(teamId);
        return new ResponseEntity<>(SuccessStatus._TEAM_MEMBER_DELETED.getMessage(), SuccessStatus._TEAM_MEMBER_DELETED.getHttpStatus());
    }
}
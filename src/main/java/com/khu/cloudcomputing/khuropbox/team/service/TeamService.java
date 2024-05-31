package com.khu.cloudcomputing.khuropbox.team.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.dto.InsertTeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamRoleDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.UserRoleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TeamService {
    /**
     * 사용자의 팀 목록을 조회합니다.
     *
     * @param userName 사용자 이름
     * @return 팀 정보 DTO 목록
     */
    List<TeamRoleDTO> findMyTeam(String userName);

    /**
     * 팀의 멤버 목록을 조회합니다.
     *
     * @param teamId 팀 ID
     * @return 사용자 엔티티 목록
     */
    List<UserRoleDTO> findTeamMember(Integer teamId);

    /**
     * 사용자가 팀에 가입합니다.
     *
     * @param info 팀 가입 정보 DTO
     * @return 성공 메시지와 상태 코드
     */
    ResponseEntity<?> joinTeam(InsertTeamDTO info);

    /**
     * 새로운 팀을 생성합니다.
     *
     * @param teamDTO 팀 정보 DTO
     * @param user 사용자 엔티티
     * @return 생성된 팀 ID와 상태 코드
     */
    ResponseEntity<?> createTeam(TeamDTO teamDTO, UserEntity user);

    /**
     * 팀의 관리자를 조회합니다.
     *
     * @param teamId 팀 ID
     * @return 사용자 엔티티
     */
    String findUserRole(String userId, Integer teamId);
    void updateRole(Integer teamId, String userName, String role);
    /**
     * 팀에서 특정 사용자를 삭제합니다.
     *
     * @param teamId 팀 ID
     * @param userId 사용자 ID
     * @return 성공 메시지와 상태 코드
     */
    ResponseEntity<?> deleteByIndex(Integer teamId, String userId);

    /**
     * 팀에서 특정 사용자를 삭제합니다 (사용자 이름 기반).
     *
     * @param teamId 팀 ID
     * @param userName 사용자 이름
     * @return 성공 메시지와 상태 코드
     */
    ResponseEntity<?> deleteByName(Integer teamId, String userName);

    /**
     * 팀을 삭제합니다.
     *
     * @param teamId 팀 ID
     * @return 성공 메시지와 상태 코드
     */
    ResponseEntity<?> deleteByTeamId(Integer teamId);
}
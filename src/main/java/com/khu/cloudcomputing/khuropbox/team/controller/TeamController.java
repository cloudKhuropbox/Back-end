package com.khu.cloudcomputing.khuropbox.team.controller;

import com.khu.cloudcomputing.khuropbox.apiPayload.ApiResponse;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import com.khu.cloudcomputing.khuropbox.team.dto.*;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    private final FilesService filesService;
    private final UserRepository userRepository;
    @GetMapping("memberlist/{teamId}")
    public ResponseEntity<?> MemberList(@PathVariable(value="teamId")Integer teamId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, teamId);
        if(role!=null) {
            List<UserRoleDTO> members = teamService.findTeamMember(teamId);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, members));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @GetMapping("list")
    public ResponseEntity<?> List(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity user=userRepository.findAllById(id).orElseThrow();
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK,teamService.findMyTeam(user.getUsername())));
    }
    @PostMapping("create")
    public ResponseEntity<?> Create(@Valid @RequestBody TeamDTO teamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity user=userRepository.findAllById(id).orElseThrow();
        return teamService.createTeam(teamDTO, user);
    }
    @PostMapping("join")
    public ResponseEntity<?> Join(@Valid @RequestBody InsertTeamDTO insertTeamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, insertTeamDTO.getTeam());
        if(role.equals(Role.admin) || role.equals(Role.owner)) {
            if(insertTeamDTO.getRole().equals(Role.owner)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad Request: Should not be 'owner'.", null));
            }
            return teamService.joinTeam(insertTeamDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("updaterole")
    public ResponseEntity<?> UpdateRole(@Valid @RequestBody UpdateRoleDTO updateRoleDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, updateRoleDTO.getTeamId());
        Role objectRole=teamService.findUserRole(userRepository.findByUsername(updateRoleDTO.getUserName()).getId(), updateRoleDTO.getTeamId());
        if(role.equals(Role.admin) || role.equals(Role.owner)) {
            if(updateRoleDTO.getRole().equals(Role.owner))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad Request: Can't update role as 'owner'.", null));
            else if(objectRole.equals(Role.owner)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad Request: role is 'owner'.", null));
            }
            teamService.updateRole(updateRoleDTO.getTeamId(), updateRoleDTO.getUserName(), updateRoleDTO.getRole());
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._USER_UPDATED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("leave/{teamId}")
    public ResponseEntity<?> Leave(@PathVariable(value="teamId") Integer teamId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, teamId);
        if(!role.equals(Role.owner)) {
            return teamService.deleteByIndex(teamId, id);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad Request: Your role is 'owner'.", null));
    }
    @PostMapping("exile")
    public ResponseEntity<?> Exile(@Valid @RequestBody InsertTeamDTO deleteTeamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id,deleteTeamDTO.getTeam());
        Role target=teamService.findUserRole(userRepository.findByUsername(deleteTeamDTO.getUserName()).getId(), deleteTeamDTO.getTeam());
        if(role.equals(Role.admin) || role.equals(Role.owner)) {
            if(target.equals(Role.customer)) {
                return teamService.deleteByName(deleteTeamDTO.getTeam(), deleteTeamDTO.getUserName());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad Request: Target is not 'customer'.", null));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("dissolution/{teamId}")
    public ResponseEntity<?> Dissolution(@PathVariable(value="teamId") Integer teamId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, teamId);
        if(role.equals(Role.owner)) {
            return teamService.deleteByTeamId(teamId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @GetMapping("filelist/{teamId}")
    public ResponseEntity<?> Files(@PathVariable(value="teamId")Integer teamId,
                                   @RequestParam(required = false, defaultValue = "0", value = "page") int pageNum,
                                   @RequestParam(required = false, defaultValue = "updatedAt", value = "orderby") String orderby,
                                   @RequestParam(required = false, defaultValue = "DESC", value = "sort") String sort,
                                   @RequestParam(required = false, defaultValue = "", value = "search")String search,
                                   @RequestParam(required = false, defaultValue = "false", value = "recycleBin")boolean recycleBin){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        Role role=teamService.findUserRole(id, teamId);
        if(role!=null) {
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, filesService.findTeamFile(teamId, orderby, pageNum, sort, search, recycleBin)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
}

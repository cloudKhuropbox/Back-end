package com.khu.cloudcomputing.khuropbox.team.controller;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import com.khu.cloudcomputing.khuropbox.team.dto.*;
import com.khu.cloudcomputing.khuropbox.team.service.TeamService;
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
        List<UserRoleDTO> members=teamService.findTeamMember(teamId);
        return ResponseEntity.ok(members);
    }
    @GetMapping("list")
    public ResponseEntity<?> List(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity user=userRepository.findAllById(id).orElseThrow();
        return ResponseEntity.ok(teamService.findMyTeam(user.getUsername()));
    }
    @PostMapping("create")
    public ResponseEntity<?> Create(@RequestBody TeamDTO teamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity user=userRepository.findAllById(id).orElseThrow();
        return ResponseEntity.ok(teamService.createTeam(teamDTO, user));
    }
    @PostMapping("join")
    public ResponseEntity<?> Join(@RequestBody InsertTeamDTO insertTeamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        String role=teamService.findUserRole(id, insertTeamDTO.getTeam());
        if(role.equals("admin") || role.equals("owner")) {
            return teamService.joinTeam(insertTeamDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("updaterole")
    public ResponseEntity<?> UpdateRole(@RequestBody UpdateRoleDTO updateRoleDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        String role=teamService.findUserRole(id, updateRoleDTO.getTeamId());
        if(role.equals("admin") || role.equals("owner")) {
            teamService.updateRole(updateRoleDTO.getTeamId(), updateRoleDTO.getUserName(), updateRoleDTO.getRole());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("leave/{teamId}")
    public ResponseEntity<?> Leave(@PathVariable(value="teamId") Integer teamId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        String role=teamService.findUserRole(id, teamId);
        if(!role.equals("owner")) {
            teamService.deleteByIndex(teamId, id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("exile")
    public ResponseEntity<?> Exile(@RequestBody InsertTeamDTO deleteTeamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        String role=teamService.findUserRole(id,deleteTeamDTO.getTeam());
        String target=teamService.findUserRole(userRepository.findByUsername(deleteTeamDTO.getUserName()).getId(), deleteTeamDTO.getTeam());
        if(role.equals("admin") || role.equals("owner")) {
            if(target.equals("customer")) {
                teamService.deleteByName(deleteTeamDTO.getTeam(), deleteTeamDTO.getUserName());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("dissolution/{teamId}")
    public ResponseEntity<?> Dissolution(@PathVariable(value="teamId") Integer teamId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        String role=teamService.findUserRole(id, teamId);
        if(role.equals("owner")) {
            teamService.deleteByTeamId(teamId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @GetMapping("filelist/{teamId}")
    public ResponseEntity<?> Files(@PathVariable(value="teamId")Integer teamId,
                                   @RequestParam(required = false, defaultValue = "0", value = "page") int pageNum,
                                   @RequestParam(required = false, defaultValue = "updatedAt", value = "orderby") String orderby,
                                   @RequestParam(required = false, defaultValue = "DESC", value = "sort") String sort){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        List<UserRoleDTO> members=teamService.findTeamMember(teamId);
        if(members.contains(userRepository.findAllById(id).orElseThrow())) {
            return ResponseEntity.ok(filesService.findTeamFile(teamId, orderby, pageNum, sort));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

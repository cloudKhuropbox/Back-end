package com.khu.cloudcomputing.khuropbox.team.controller;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import com.khu.cloudcomputing.khuropbox.team.dto.InsertTeamDTO;
import com.khu.cloudcomputing.khuropbox.team.dto.TeamDTO;
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
        List<UserEntity> members=teamService.findTeamMember(teamId);
        if(members.contains(userRepository.findAllById(id).orElseThrow())){
            return ResponseEntity.ok(members);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @GetMapping("list/{userName}")
    public ResponseEntity<?> List(@PathVariable(value = "userName")String userName){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity user=userRepository.findAllById(id).orElseThrow();
        if(user.getUsername().equals(userName)){
            return ResponseEntity.ok(teamService.findMyTeam(userName));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
        UserEntity admin=teamService.findTeamAdmin(insertTeamDTO.getTeam());
        if(id.equals(admin.getId())) {
            return ResponseEntity.ok(teamService.joinTeam(insertTeamDTO));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("leave")
    public ResponseEntity<?> Leave(@RequestBody TeamDTO teamDTO){
        List<UserEntity> members=teamService.findTeamMember(teamDTO.getTeamId());
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        if(members.contains(userRepository.findAllById(id).orElseThrow())){
            teamService.deleteByIndex(teamDTO.getTeamId(), id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("exile")
    public ResponseEntity<?> Exile(@RequestBody InsertTeamDTO deleteTeamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity admin=teamService.findTeamAdmin(deleteTeamDTO.getTeam());
        if(id.equals(admin.getId())) {
            teamService.deleteByName(deleteTeamDTO.getTeam(), deleteTeamDTO.getUserName());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("dissolution")
    public ResponseEntity<?> Dissolution(@RequestBody TeamDTO teamDTO){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        UserEntity admin=teamService.findTeamAdmin(teamDTO.getTeamId());
        if(id.equals(admin.getId())) {
            teamService.deleteByTeamId(teamDTO.getTeamId());
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
        List<UserEntity> members=teamService.findTeamMember(teamId);
        if(members.contains(userRepository.findAllById(id).orElseThrow())) {
            return ResponseEntity.ok(filesService.findTeamFile(teamId, orderby, pageNum, sort));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

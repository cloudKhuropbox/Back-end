package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import com.khu.cloudcomputing.khuropbox.team.entity.UserTeam;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTeamDTO {
    private Integer id;
    @NotNull
    private UserEntity user;
    @NotNull
    private Team team;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
    public UserTeamDTO(UserTeam entity){
        this.id=entity.getUserTeamId();
        this.user=entity.getUser();
        this.team=entity.getTeam();
        this.role=entity.getRole();
    }
    public UserTeam toEntity(){
        return UserTeam.builder()
                .user(user)
                .team(team)
                .role(role)
                .build();
    }
}

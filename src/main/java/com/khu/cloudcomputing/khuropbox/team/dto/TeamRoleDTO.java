package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamRoleDTO {
    @NotNull
    private Team team;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
    public TeamRoleDTO(TeamRoleMapping entity){
        this.team=entity.getTeam();
        this.role=entity.getRole();
    }
}

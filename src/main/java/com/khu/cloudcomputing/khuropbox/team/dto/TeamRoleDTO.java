package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamRoleDTO {
    private Team team;
    private String role;
    public TeamRoleDTO(TeamRoleMapping entity){
        this.team=entity.getTeam();
        this.role=entity.getRole();
    }
}

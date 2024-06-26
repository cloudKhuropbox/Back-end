package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.team.entity.Team;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamDTO {
    private Integer teamId;
    @NotNull
    private String teamName;
    public TeamDTO(Team entity){
        this.teamId=entity.getTeamId();
        this.teamName=entity.getTeamName();
    }

    public Team toEntity(){
        return Team.builder()
                .teamName(teamName)
                .build();
    }
}

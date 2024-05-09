package com.khu.cloudcomputing.khuropbox.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InsertTeamDTO {
    private String userName;
    private Integer team;
    private String role;
}

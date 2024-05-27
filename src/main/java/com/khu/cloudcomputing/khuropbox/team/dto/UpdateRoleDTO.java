package com.khu.cloudcomputing.khuropbox.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleDTO {
    private Integer teamId;
    private String userName;
    private String role;
}
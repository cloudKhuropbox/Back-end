package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import com.khu.cloudcomputing.khuropbox.team.entity.Team;

public interface TeamRoleMapping {
    Team getTeam();
    Role getRole();
}

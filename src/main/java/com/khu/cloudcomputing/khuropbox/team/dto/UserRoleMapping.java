package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;

public interface UserRoleMapping {
    UserEntity getUser();
    Role getRole();
}

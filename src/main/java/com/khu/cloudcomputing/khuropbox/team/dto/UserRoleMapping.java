package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;

public interface UserRoleMapping {
    UserEntity getUser();
    String getRole();
}

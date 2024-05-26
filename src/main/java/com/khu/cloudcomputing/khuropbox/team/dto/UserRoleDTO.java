package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRoleDTO {
    private UserEntity user;
    private String role;
    public UserRoleDTO(UserRoleMapping entity){
        this.user=entity.getUser();
        this.role=entity.getRole();
    }
}

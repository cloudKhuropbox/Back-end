package com.khu.cloudcomputing.khuropbox.team.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.team.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRoleDTO {
    @NotNull
    private UserEntity user;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
    public UserRoleDTO(UserRoleMapping entity){
        this.user=entity.getUser();
        this.role=entity.getRole();
    }
}

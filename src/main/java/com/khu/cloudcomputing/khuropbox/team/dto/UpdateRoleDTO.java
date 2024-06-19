package com.khu.cloudcomputing.khuropbox.team.dto;

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
public class UpdateRoleDTO {
    @NotNull
    private Integer teamId;
    @NotNull
    private String userName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
}
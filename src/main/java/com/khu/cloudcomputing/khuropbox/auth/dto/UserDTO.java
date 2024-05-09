package com.khu.cloudcomputing.khuropbox.auth.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String token;
    private String username;
    private String password;
    private String id;
}

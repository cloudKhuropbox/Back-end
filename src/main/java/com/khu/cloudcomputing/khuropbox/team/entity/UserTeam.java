package com.khu.cloudcomputing.khuropbox.team.entity;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userTeamId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id")
    @NotNull
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="teamId")
    @NotNull
    private Team team;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;
}

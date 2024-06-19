package com.khu.cloudcomputing.khuropbox.team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamId;
    @NotNull
    private String teamName;
    public Team update(String teamName){
        this.teamName=teamName;
        return this;
    }
}

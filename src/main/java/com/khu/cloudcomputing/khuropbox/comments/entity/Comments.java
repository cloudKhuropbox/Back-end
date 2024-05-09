package com.khu.cloudcomputing.khuropbox.comments.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    private Integer fileId;
    private String comment;
    private LocalDateTime createdAt;
    private Boolean updated;
    public Comments update(String comment){
        this.comment=comment;
        this.updated=true;
        return this;
    }
}

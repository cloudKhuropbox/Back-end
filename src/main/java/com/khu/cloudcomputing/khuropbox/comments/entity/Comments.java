package com.khu.cloudcomputing.khuropbox.comments.entity;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name="userId")
    private UserEntity user;
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

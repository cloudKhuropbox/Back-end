package com.khu.cloudcomputing.khuropbox.comments.entity;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId")
    @NotNull
    private UserEntity user;
    @NotNull
    private Integer fileId;
    @NotNull
    private String comment;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Boolean updated;
    private Integer replyId;
    public Comments update(String comment){
        this.comment=comment;
        this.updated=true;
        return this;
    }
}

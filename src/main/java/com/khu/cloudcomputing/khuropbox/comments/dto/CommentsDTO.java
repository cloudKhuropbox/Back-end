package com.khu.cloudcomputing.khuropbox.comments.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentsDTO {//댓글 입력시에만 사용
    private Integer id;
    @NotNull
    private UserEntity user;
    @NotNull
    private Integer fileId;
    @NotBlank
    private String comment;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Boolean updated;
    private Integer replyId;
    public CommentsDTO(Comments entity){
        this.id=entity.getId();
        this.user=entity.getUser();
        this.fileId=entity.getFileId();
        this.comment=entity.getComment();
        this.createdAt=entity.getCreatedAt();
        this.updated=entity.getUpdated();
        this.replyId=entity.getReplyId();
    }

    public Comments toEntity(){
        return Comments.builder()
                .user(user)
                .comment(comment)
                .fileId(fileId)
                .createdAt(createdAt)
                .updated(updated)
                .replyId(replyId)
                .build();
    }
}

package com.khu.cloudcomputing.khuropbox.comments.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentsInfoDTO {//댓글 조회할때만 사용
    @NotNull
    private Integer id;
    @NotBlank
    private String userName;
    @NotNull
    private Integer fileId;
    @NotBlank
    private String comment;
    @NotNull
    private LocalDateTime createdAt;
    private Boolean updated;
    private Integer replyId;
    public CommentsInfoDTO(Comments entity){
        this.id=entity.getId();
        this.userName=entity.getUser().getUsername();
        this.fileId=entity.getFileId();
        this.comment=entity.getComment();
        this.createdAt=entity.getCreatedAt();
        this.updated=entity.getUpdated();
        this.replyId=entity.getReplyId();
    }
}

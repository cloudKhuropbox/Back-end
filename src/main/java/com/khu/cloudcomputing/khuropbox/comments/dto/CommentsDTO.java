package com.khu.cloudcomputing.khuropbox.comments.dto;

import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentsDTO {
    private Integer id;
    private String userName;
    private Integer fileId;
    private String comment;
    private LocalDateTime createdAt;
    private Boolean updated;
    public CommentsDTO(Comments entity){
        this.id=entity.getId();
        this.userName=entity.getUserName();
        this.fileId=entity.getFileId();
        this.comment=entity.getComment();
        this.createdAt=entity.getCreatedAt();
        this.updated=entity.getUpdated();
    }

    public Comments toEntity(){
        return Comments.builder()
                .userName(userName)
                .comment(comment)
                .fileId(fileId)
                .createdAt(createdAt)
                .updated(updated)
                .build();
    }
}

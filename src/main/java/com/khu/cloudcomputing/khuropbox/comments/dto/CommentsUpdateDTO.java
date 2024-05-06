package com.khu.cloudcomputing.khuropbox.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentsUpdateDTO {
    private Integer id;
    private String comment;
}
package com.khu.cloudcomputing.khuropbox.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentsUpdateDTO {
    @NotNull
    private Integer id;
    @NotBlank
    private String comment;
}
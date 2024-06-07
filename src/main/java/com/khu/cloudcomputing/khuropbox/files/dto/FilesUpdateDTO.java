package com.khu.cloudcomputing.khuropbox.files.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilesUpdateDTO {
    @NotNull
    private Integer id;
    private String fileName;
    private String fileLink;
    private Integer teamId;
    private String changeDescription;
}

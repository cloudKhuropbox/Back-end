package com.khu.cloudcomputing.khuropbox.files.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilesUpdateDTO {
    private Integer id;
    private String fileName;
    private String fileLink;
    private String changeDescription;
}

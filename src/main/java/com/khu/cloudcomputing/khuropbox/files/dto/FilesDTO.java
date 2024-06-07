package com.khu.cloudcomputing.khuropbox.files.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
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
public class FilesDTO {
    private Integer id;
    @NotBlank
    private String fileName;
    @NotBlank
    private String fileLink;
    @NotNull
    private Long fileSize;
    private String fileType;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    @NotNull
    private UserEntity owner;
    private Integer teamId;
    private Boolean isRecycleBin;

    public FilesDTO(Files entity){
        this.id=entity.getId();
        this.fileName=entity.getFileName();
        this.fileLink=entity.getFileLink();
        this.fileSize=entity.getFileSize();
        this.fileType=entity.getFileType();
        this.createdAt=entity.getCreatedAt();
        this.updatedAt=entity.getUpdatedAt();
        this.owner=entity.getOwner();
        this.teamId=entity.getTeamId();
        this.isRecycleBin=entity.getIsRecycleBin();
    }

    public Files toEntity(){
        return Files.builder()
                .fileName(fileName)
                .fileLink(fileLink)
                .fileSize(fileSize)
                .fileType(fileType)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .owner(owner)
                .teamId(teamId)
                .build();
    }
}

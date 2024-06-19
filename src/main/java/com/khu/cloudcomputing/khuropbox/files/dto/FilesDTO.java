package com.khu.cloudcomputing.khuropbox.files.dto;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import jakarta.servlet.http.Part;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FilesDTO {
    private Integer id;
    @NotBlank
    private String fileName;
    @NotBlank
    private String fileKey;
    @NotNull
    private Long fileSize;
    private String fileType;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private UserEntity owner;
    private Integer teamId;
    private Boolean isRecycleBin;
    private List<PartsDTO> parts;

    public FilesDTO(Files entity){
        this.id=entity.getId();
        this.fileName=entity.getFileName();
        this.fileKey=entity.getFileKey();
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
                .fileKey(fileKey)
                .fileSize(fileSize)
                .fileType(fileType)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .owner(owner)
                .teamId(teamId)
                .isRecycleBin(isRecycleBin)
                .build();
    }
}

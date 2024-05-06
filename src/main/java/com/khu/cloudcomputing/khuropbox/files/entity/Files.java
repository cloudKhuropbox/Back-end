package com.khu.cloudcomputing.khuropbox.files.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Files {
    @Id
    @GeneratedValue
    private Integer id;
    private String fileName;
    @NonNull
    private String fileLink;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Files update(String fileName, String fileLink, LocalDateTime updatedAt){
        this.fileName=fileName;
        this.fileLink=fileLink;
        this.updatedAt=updatedAt;
        return this;
    }
    public Files updateLink(String fileLink){
        this.fileLink=fileLink;
        return this;
    }
}

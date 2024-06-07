package com.khu.cloudcomputing.khuropbox.files.entity;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.stt.entity.ScriptEntity;
import com.khu.cloudcomputing.khuropbox.summarygenerator.entity.GptSummaryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileName;
    @NotNull
    private String fileLink;
    @NotNull
    @Size(min=0)
    private Long fileSize;
    private String fileType;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    @NotNull
    private UserEntity owner;
    private Integer teamId;
    private Boolean isRecycleBin;

    public Files update(String fileName, LocalDateTime updatedAt, Integer teamId){
        this.fileName=fileName;
        this.updatedAt=updatedAt;
        this.teamId=teamId;
        return this;
    }
    public Files recycleBin(){
        this.isRecycleBin=true;
        return this;
    }
    public Files restore(){
        this.isRecycleBin=false;
        return this;
    }
    public Files updateLink(String fileLink){
        this.fileLink=fileLink;
        return this;
    }

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FileHistoryEntity> fileHistory = new ArrayList<FileHistoryEntity>();

    @OneToOne(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ScriptEntity scriptEntity;

    @OneToOne(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GptSummaryEntity summaryEntity;
}

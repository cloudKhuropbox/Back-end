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
    private String fileKey;
    @NotNull
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
    private UserEntity owner;
    private Integer teamId;
    private Boolean isRecycleBin;

    public void update(String fileName, LocalDateTime updatedAt, Integer teamId){
        this.fileName=fileName;
        this.updatedAt=updatedAt;
        this.teamId=teamId;
    }
    public void recycleBin(){
        this.isRecycleBin=true;
    }
    public void restore(){
        this.isRecycleBin=false;
    }
    public void updateKey(String fileKey){
        this.fileKey=fileKey;
    }

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FileHistoryEntity> fileHistory = new ArrayList<FileHistoryEntity>();

    @OneToOne(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ScriptEntity scriptEntity;

    @OneToOne(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GptSummaryEntity summaryEntity;
}

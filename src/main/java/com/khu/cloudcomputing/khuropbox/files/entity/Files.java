package com.khu.cloudcomputing.khuropbox.files.entity;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.sql.In;

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
    @NonNull
    private String fileLink;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="userId")
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
}

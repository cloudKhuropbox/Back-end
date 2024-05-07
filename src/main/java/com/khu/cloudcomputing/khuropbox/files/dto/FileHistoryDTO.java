package com.khu.cloudcomputing.khuropbox.files.dto;

import com.khu.cloudcomputing.khuropbox.files.entity.FileHistoryEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileHistoryDTO {
    private Integer historyId;
    private Integer fileId;
    private String fileName;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String changeDescription;

    public FileHistoryDTO(Files fileEntity, FileHistoryEntity historyEntity) {
        this.historyId = historyEntity.getHistory_id();
        this.fileId=fileEntity.getId();
        this.fileName=fileEntity.getFileName();
        this.changeDescription=historyEntity.getChangeDescription();
        this.created_at=fileEntity.getCreatedAt();
        this.updated_at=fileEntity.getUpdatedAt();
    }
}

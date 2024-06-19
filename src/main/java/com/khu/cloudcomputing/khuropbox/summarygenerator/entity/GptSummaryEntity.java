package com.khu.cloudcomputing.khuropbox.summarygenerator.entity;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class GptSummaryEntity {
    @Id
    private Integer fileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Files file;

    private String s3FileKey;
}

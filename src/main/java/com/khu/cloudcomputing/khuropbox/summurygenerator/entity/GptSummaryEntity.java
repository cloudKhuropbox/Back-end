package com.khu.cloudcomputing.khuropbox.summurygenerator.entity;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GptSummaryEntity {
    @Id
    private Long fileId;

    private String fileKey;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Files file;

    private String summary;

    @Lob
    private String fullResponse;
}


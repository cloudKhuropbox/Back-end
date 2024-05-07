package com.khu.cloudcomputing.khuropbox.files.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer history_id;

    @ManyToOne
    @JoinColumn(name="fileid", referencedColumnName ="id", nullable = false)
    private Files file;

    private LocalDateTime changeDate;

    @Column(columnDefinition = "TEXT")
    private String changeDescription;

    public void updateFileHistory(Files file, String changeDescription) {
        this.file = file;
        this.changeDate = LocalDateTime.now();
        this.changeDescription = changeDescription;
    }
}

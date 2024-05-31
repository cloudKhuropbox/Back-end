package com.khu.cloudcomputing.khuropbox.stt.entity;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "scripts")
public class ScriptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    private Files file;

    @Lob
    private String scriptContent;

    private LocalDateTime createdAt;
}
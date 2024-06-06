package com.khu.cloudcomputing.khuropbox.stt.entity;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scripts")
public class ScriptEntity {
    @Id
    private Integer fileId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Files file;

    private String requestId;

    private String Results;
}
package com.khu.cloudcomputing.khuropbox.stt.repository;

import com.khu.cloudcomputing.khuropbox.stt.entity.ScriptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SttRepository extends JpaRepository<ScriptEntity, Integer> {
    ScriptEntity findByTranscribeId(String transcribeId);
}

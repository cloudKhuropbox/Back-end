package com.khu.cloudcomputing.khuropbox.stt.service;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.repository.FilesRepository;
import com.khu.cloudcomputing.khuropbox.stt.entity.ScriptEntity;
import com.khu.cloudcomputing.khuropbox.stt.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScriptService {
    private final ScriptRepository scriptRepository;
    private final FilesRepository filesRepository;

    @Transactional
    public ScriptEntity saveScript(Integer fileId, String scriptContent) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        ScriptEntity scriptEntity = new ScriptEntity();
        scriptEntity.setFile(file);
        scriptEntity.setScriptContent(scriptContent);
        scriptEntity.setCreatedAt(LocalDateTime.now());

        return scriptRepository.save(scriptEntity);
    }
}

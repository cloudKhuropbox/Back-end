package com.khu.cloudcomputing.khuropbox.stt.controller;

import com.khu.cloudcomputing.khuropbox.stt.dto.TranscribeResultDTO;
import com.khu.cloudcomputing.khuropbox.stt.dto.TranscribeStatusDTO;
import com.khu.cloudcomputing.khuropbox.stt.entity.ScriptEntity;
import com.khu.cloudcomputing.khuropbox.stt.repository.SttRepository;
import com.khu.cloudcomputing.khuropbox.stt.service.SttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@Slf4j
public class SttRequestController {

    private final SttService sttService;
    private final SttRepository sttRepository;

    public SttRequestController(SttService sttService, SttRepository sttRepository) {
        this.sttService = sttService;
        this.sttRepository = sttRepository;
    }


    @PostMapping("/{fileId}")
    public Mono<ResponseEntity<String>> transcribeFile(@PathVariable(value="fileId") Integer fileId, @RequestParam(value="speakerCount") int speakerCount) {
        return sttService.transcribe(fileId, speakerCount)
                .map(response -> {
                    ScriptEntity scriptEntity = new ScriptEntity();
                    scriptEntity.setFileId(fileId);
                    scriptEntity.setTranscribeId(response.getTranscribeId());
                    sttRepository.save(scriptEntity);
                    return ResponseEntity.ok("{\"transcribeId\":\"" + response.getTranscribeId() + "\"}");
                })
                .onErrorResume(e -> {
                    log.error("Error during transcription request: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패"));
                });
    }

    @Scheduled(fixedDelay = 10)
    public void pollTranscriptionResults() {
        Iterable<ScriptEntity> scripts = sttRepository.findAll();
        scripts.forEach(script -> {
            if (script.getScriptContent() == null) {
                sttService.pollTranscription(script.getTranscribeId())
                        .flatMap(response -> {
                            if (response instanceof TranscribeStatusDTO statusResponse) {
                                if ("completed".equals(statusResponse.getStatus())) {
                                    return sttService.pollTranscription(script.getTranscribeId());
                                } else {
                                    return Mono.empty();
                                }
                            } else if (response instanceof TranscribeResultDTO resultResponse) {
                                script.setScriptContent(resultResponse.getUtterances().toString());
                                sttRepository.save(script);
                                log.info("Transcription completed for fileId: {}", script.getFileId());
                                return Mono.empty();
                            } else {
                                return Mono.empty();
                            }
                        })
                        .onErrorResume(e -> {
                            log.error("Error polling transcription result: ", e);
                            return Mono.empty();
                        })
                        .subscribe();
            }
        });
    }

    @GetMapping("/{transcribeId}")
    public ResponseEntity<?> getTranscriptionResult(@PathVariable String transcribeId) {
        Optional<ScriptEntity> scriptEntity = sttRepository.findByTranscribeId(transcribeId);
        if (scriptEntity.isPresent()) {
            return ResponseEntity.ok(scriptEntity.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transcription result not found");
        }
    }
}

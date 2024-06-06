package com.khu.cloudcomputing.khuropbox.stt.controller;

import com.khu.cloudcomputing.khuropbox.stt.dto.SttResponseDTO;
import com.khu.cloudcomputing.khuropbox.stt.service.SttService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
public class SttRequestController {

    private final SttService sttService;

    public SttRequestController(SttService sttService) {
        this.sttService = sttService;
    }

    @PostMapping("/requestTranscribe")
    public Mono<ResponseEntity<SttResponseDTO>> requestTranscribe(@RequestParam(required = false, defaultValue="0")int speakerCount,
                                                                  @RequestParam int fileId ) throws JSONException, IOException {
        return sttService.transcribe(fileId,speakerCount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

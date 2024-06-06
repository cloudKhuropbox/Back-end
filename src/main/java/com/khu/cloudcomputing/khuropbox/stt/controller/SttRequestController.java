package com.khu.cloudcomputing.khuropbox.stt.controller;

import com.khu.cloudcomputing.khuropbox.stt.dto.SttResponseDTO;
import com.khu.cloudcomputing.khuropbox.stt.dto.SttResultDTO;
import com.khu.cloudcomputing.khuropbox.stt.service.SttService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
@RequestMapping("/sttService")
public class SttRequestController {

    private final SttService sttService;

    public SttRequestController(SttService sttService) {
        this.sttService = sttService;
    }

    @PostMapping("/requestTranscribe")
    public Mono<ResponseEntity<SttResponseDTO>> requestTranscribe(@RequestParam(required = false, defaultValue="1")int speakerCount,
                                                                  @RequestParam int fileId ) throws JSONException, IOException {
        return sttService.transcribe(fileId,speakerCount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping("/getTranscribeResult")
    public Mono<ResponseEntity<SttResultDTO>> getTranscribeResult(@RequestParam String requestId) throws JSONException, IOException {
        return sttService.getTranscribeResult(requestId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

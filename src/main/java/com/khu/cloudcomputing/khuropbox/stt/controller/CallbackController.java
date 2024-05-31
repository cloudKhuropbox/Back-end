package com.khu.cloudcomputing.khuropbox.stt.controller;

import com.khu.cloudcomputing.khuropbox.apiPayload.ApiResponse;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.stt.service.ScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class CallbackController {
    private final ScriptService scriptService;

    @PostMapping("/transcription")
    public ResponseEntity<ApiResponse<String>> handleTranscriptionCallback(
            @RequestParam Integer fileId,
            @RequestBody String scriptContent) {
        scriptService.saveScript(fileId, scriptContent);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, "Script saved successfully"));
    }
}
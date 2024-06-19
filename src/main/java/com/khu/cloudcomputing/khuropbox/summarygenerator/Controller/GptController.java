package com.khu.cloudcomputing.khuropbox.summarygenerator.Controller;

import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.GptResponseDTO;
import com.khu.cloudcomputing.khuropbox.summarygenerator.service.GptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gpt")
public class GptController{

    private final GptService gptService;

    private final String model;

    public GptController(GptService gptService, @Value("${openai.model}")String model) {
        this.gptService = gptService;
        this.model = model;
    }

    @GetMapping("/summarize")
    public Mono<ResponseEntity<GptResponseDTO>> summarizeText(@RequestParam(value = "fileId") Integer fileId) {
        return gptService.summarizeScript(model, fileId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
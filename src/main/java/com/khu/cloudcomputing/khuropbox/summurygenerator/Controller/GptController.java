package com.khu.cloudcomputing.khuropbox.summurygenerator.Controller;

import com.khu.cloudcomputing.khuropbox.summurygenerator.dto.GptResponseDTO;
import com.khu.cloudcomputing.khuropbox.summurygenerator.service.GptService;
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
    public Mono<ResponseEntity<GptResponseDTO>> summarizeText(@RequestParam String fileKey) {
        return gptService.summarizeScript(model, fileKey)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
package com.khu.cloudcomputing.khuropbox.summarygenerator.service;

import com.khu.cloudcomputing.khuropbox.configuration.aws.AwsService;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.repository.FilesRepository;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.GptRequestDTO;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.GptResponseDTO;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.Message;
import com.khu.cloudcomputing.khuropbox.summarygenerator.entity.GptSummaryEntity;
import com.khu.cloudcomputing.khuropbox.summarygenerator.repository.GptSummaryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class GptService {

    private final AwsService awsService;
    private final WebClient webClient;

    private final String url;
    private final String apiKey;
    private final GptSummaryRepository gptSummaryRepository;
    private final FilesRepository filesRepository;

    public GptService(AwsService awsService, WebClient webClient, @Value("${openai.api.url}")String url, @Value("${openai.secret-key}")String apiKey, GptSummaryRepository gptSummaryRepository, FilesRepository filesRepository) {
        this.awsService = awsService;
        this.webClient = webClient;
        this.url = url;
        this.apiKey = apiKey;
        this.gptSummaryRepository = gptSummaryRepository;
        this.filesRepository = filesRepository;
    }

    public Mono<GptResponseDTO> summarizeScript(String model, Integer fileId) {
        return Mono.fromCallable(() -> filesRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found")))
                .flatMap(file -> awsService.readFileAsString(file.getFileKey())
                        .flatMap(script -> {

                            String prompt = "Summarize the following meeting transcript, including main topics, detailed agenda items, and conclusions:\n\n" + script;
                            GptRequestDTO requestDTO = GptRequestDTO.builder()
                                    .model(model)
                                    .message(new Message("system", prompt))
                                    .temperature(0.4f)
                                    .topP(0.9f)
                                    .frequencyPenalty(0.1f)
                                    .presencePenalty(0.1f)
                                    .build();

                            return sendGptRequest(requestDTO)
                                    .flatMap(responseDTO -> {
                                        String summary = responseDTO.getMessage().getContent();
                                        String fileName = UUID.randomUUID() + ".txt";

                                        return awsService.uploadFile(fileName, summary.getBytes(StandardCharsets.UTF_8))
                                                .flatMap(s3Key -> saveSummaryToDatabase(file, s3Key))
                                                .thenReturn(responseDTO);
                                    });
                        }));
    }

    private Mono<String> saveSummaryToDatabase(Files file, String s3Key) {
        return Mono.fromCallable(() -> {
            GptSummaryEntity summaryEntity = new GptSummaryEntity(file.getId(), file, s3Key);
            gptSummaryRepository.save(summaryEntity);
            return s3Key;
        });
    }

    private Mono<GptResponseDTO> sendGptRequest(GptRequestDTO requestDTO) {
        return webClient.post()
                .uri(url)
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(GptResponseDTO.class);
    }
}

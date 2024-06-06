package com.khu.cloudcomputing.khuropbox.summarygenerator.service;

import com.khu.cloudcomputing.khuropbox.configuration.aws.AwsService;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.GptRequestDTO;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.GptResponseDTO;
import com.khu.cloudcomputing.khuropbox.summarygenerator.dto.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GptService {

    private final AwsService awsService;
    private final WebClient webClient;

    private final String url;
    private final String apiKey;

    public GptService(AwsService awsService, WebClient webClient, @Value("${openai.api.url}")String url, @Value("${openai.secret-key}")String apiKey) {
        this.awsService = awsService;
        this.webClient = webClient;
        this.url = url;
        this.apiKey = apiKey;
    }

    public Mono<GptResponseDTO> summarizeScript(String model, String fileKey) {
        // S3에서 파일을 읽어서 문자열로 변환
        return awsService.readFileAsString(fileKey)
                .flatMap(script -> {
                    // API 요청을 위한 프롬프트 문자열 생성
                    String prompt = "Summarize the following meeting transcript, including main topics, detailed agenda items, and conclusions:\n\n" + script;

                    // GPT 요청 DTO 생성
                    GptRequestDTO requestDTO = GptRequestDTO.builder()
                            .model(model)
                            .message(new Message("system", prompt))
                            .temperature(0.4f)
                            .topP(0.9f)
                            .frequencyPenalty(0.1f)
                            .presencePenalty(0.1f)
                            .build();

                    return sendGptRequest(requestDTO);
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

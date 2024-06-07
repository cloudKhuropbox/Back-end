package com.khu.cloudcomputing.khuropbox.stt.service;

import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
import com.khu.cloudcomputing.khuropbox.configuration.aws.AwsService;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.repository.FilesRepository;
import com.khu.cloudcomputing.khuropbox.stt.auth.ReturnzeroAuthService;
import com.khu.cloudcomputing.khuropbox.stt.dto.*;
import com.khu.cloudcomputing.khuropbox.stt.entity.ScriptEntity;
import com.khu.cloudcomputing.khuropbox.stt.repository.SttRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SttService {
    private final ReturnzeroAuthService returnzeroAuthService;
    private final FilesRepository filesRepository;
    private final AwsService awsService;
    private final WebClient webClient;
    private final String url;
    private final SttRepository sttRepository;
    private final ScriptEntity scriptEntity;

    public SttService(ReturnzeroAuthService returnzeroAuthService, FilesRepository filesRepository, AwsService awsService, WebClient webClient, @Value("${returnzero.apiUrl}") String url, SttRepository sttRepository, ScriptEntity scriptEntity) {
        this.returnzeroAuthService = returnzeroAuthService;
        this.filesRepository = filesRepository;
        this.awsService = awsService;
        this.webClient = webClient;
        this.url = url;
        this.sttRepository = sttRepository;
        this.scriptEntity =scriptEntity;
    }

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("mp4", "m4a", "mp3", "amr", "flac", "wav");

    public Mono<byte[]> getFileData(Integer fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", ErrorStatus._FILE_NOT_FOUND.getHttpStatus()));

        if (!isSupportedFormat(file.getFileType())) {
            throw new GeneralException(ErrorStatus._UNSUPPORTED_MEDIA_TYPE.getCode(), "Unsupported file format", ErrorStatus._UNSUPPORTED_MEDIA_TYPE.getHttpStatus());
        }

        return awsService.readFileAsByteArray(file.getFileLink())
                .onErrorMap(IOException.class, e -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Error reading file from S3", ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus()));
    }

    private boolean isSupportedFormat(String fileType) {
        return SUPPORTED_FORMATS.contains(fileType.toLowerCase());
    }

    private SttRequestDTO buildSttRequestDTO(Mono<byte[]> fileData, SttRequestConfig sttRequestConfig) {
        return SttRequestDTO.builder()
                .file(fileData)
                .config(sttRequestConfig)
                .build();
    }

    private SttRequestConfig buildSttRequestConfig(int speakerCount) {
        DiarizationConfig diarizationConfig = DiarizationConfig.builder()
                .spk_count(speakerCount).build();

        return SttRequestConfig.builder()
                .useDiarization(true)
                .diarization(diarizationConfig)
                .useParagraphSplitter(false)
                .build();
    }

    // 비동기적으로 토큰을 확인한 후에 요청을 보내도록 수정
    public Mono<TranscribeResponseDTO> sendPostRequest(SttRequestDTO sttRequestDTO) {
        return returnzeroAuthService.checkValidToken()
                .flatMap(token -> webClient.post()
                        .uri(url)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .bodyValue(sttRequestDTO)
                        .retrieve()
                        .bodyToMono(TranscribeResponseDTO.class)
                        .onErrorMap(IOException.class, e -> new GeneralException(ErrorStatus._TRANSCRIBE_REQUEST_FAILED.getCode(), "Error sending post request", ErrorStatus._TRANSCRIBE_REQUEST_FAILED.getHttpStatus()))
                        .onErrorMap(org.springframework.boot.configurationprocessor.json.JSONException.class, e -> new GeneralException(ErrorStatus._TRANSCRIBE_REQUEST_FAILED.getCode(), "Error parsing JSON response", ErrorStatus._TRANSCRIBE_REQUEST_FAILED.getHttpStatus())));
    }

    public Mono<TranscribeResponseDTO> transcribe(int fileId, int speakerCount) {
        Mono<byte[]> fileData = getFileData(fileId);
        SttRequestConfig sttRequestConfig = buildSttRequestConfig(speakerCount);
        SttRequestDTO requestDTO = buildSttRequestDTO(fileData, sttRequestConfig);

        return sendPostRequest(requestDTO);
    }

    // 비동기적으로 토큰을 확인한 후 요청을 보내도록 수정
    public Mono<Object> pollTranscription(String transcribeId) {
        return returnzeroAuthService.checkValidToken()
                .flatMap(token -> webClient.get()
                        .uri(url + "/" + transcribeId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> {
                            try {
                                if (response.contains("\"status\":\"completed\"")) {
                                    return webClient.get()
                                            .uri(url + "/" + transcribeId)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .header("Authorization", "Bearer " + token)
                                            .retrieve()
                                            .bodyToMono(TranscribeResultDTO.class)
                                            .flatMap(transcribeResultDTO -> {
                                                Optional<ScriptEntity> scriptEntity = sttRepository.findByTranscribeId(transcribeId);
                                                if
                                            });
                                } else {
                                    return webClient.get()
                                            .uri(url + "/" + transcribeId)
                                            .accept(MediaType.APPLICATION_JSON)
                                            .header("Authorization", "Bearer " + token)
                                            .retrieve()
                                            .bodyToMono(TranscribeStatusDTO.class)
                                            .cast(Object.class);
                                }
                            } catch (Exception e) {
                                return Mono.error(new GeneralException(ErrorStatus._TRANSCRIBE_POLLING_FAILED.getCode(), "Error processing response", ErrorStatus._TRANSCRIBE_POLLING_FAILED.getHttpStatus()));
                            }
                        }));
    }
}

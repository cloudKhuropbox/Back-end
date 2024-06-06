package com.khu.cloudcomputing.khuropbox.stt.service;

import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
import com.khu.cloudcomputing.khuropbox.configuration.aws.AwsService;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.repository.FilesRepository;
import com.khu.cloudcomputing.khuropbox.stt.auth.ReturnzeroAuthService;
import com.khu.cloudcomputing.khuropbox.stt.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SttService {
    private final ReturnzeroAuthService returnzeroAuthService;
    private final FilesRepository filesRepository;
    private final AwsService awsService;
    private final WebClient webClient;

    private final String apiUrl;

    public SttService(ReturnzeroAuthService returnzeroAuthService, FilesRepository filesRepository, AwsService awsService, WebClient webClient,
                      @Value("${returnzero.apiUrl}") String url) {
        this.returnzeroAuthService = returnzeroAuthService;
        this.filesRepository = filesRepository;
        this.awsService = awsService;
        this.webClient = webClient;
        this.apiUrl = url;
    }

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("mp4", "m4a", "mp3", "amr", "flac", "wav");


    //s3에서 파일을 찾아서 바이너리로 변환(파일이 없거나 api에서 처리하지 못하는 파일 타입이면 여기서 거름)
    public Mono<byte[]> getFileData(Integer fileId) throws GeneralException {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", HttpStatus.NOT_FOUND));

        // 파일 형식 검증
        if (!isSupportedFormat(file.getFileType())) {
            throw new GeneralException(ErrorStatus._UNSUPPORTED_MEDIA_TYPE.getCode(), "Unsupported file format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // S3에서 파일 가져오기
        return awsService.readFileAsByteArray(file.getFileLink());
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


    //api 요청담당-엔드포인트, 헤더설정->요청 전송
    public Mono<SttResponseDTO> sendPostRequest(SttRequestDTO sttRequestDTO) throws IOException, JSONException {
        String token = returnzeroAuthService.checkValidToken();
        return webClient.post()
                .uri(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(sttRequestDTO)
                .retrieve()
                .bodyToMono(SttResponseDTO.class);
    }

    public Mono<SttResultDTO> sendGetRequest(String requestId) throws IOException, JSONException {
        String token = returnzeroAuthService.checkValidToken();
        return webClient.get()
                .uri(apiUrl+"/"+requestId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(SttResultDTO.class);
    }

    public Mono<SttResponseDTO> transcribe(int fileId,int speakerCount) throws IOException, JSONException {
        Mono<byte[]> fileData = getFileData(fileId);
        SttRequestConfig sttRequestConfig = buildSttRequestConfig(speakerCount);
        SttRequestDTO requestDTO = buildSttRequestDTO(fileData, sttRequestConfig);

        return sendPostRequest(requestDTO);
    }

    public Mono<SttResultDTO> getTranscribeResult(String requestId) throws IOException, JSONException {
        return sendGetRequest(requestId);
    }

}

package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.*;
import org.apache.http.client.config.RequestConfig;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class SttRequestDTO {
    private Mono<byte[]> file;
    private SttRequestConfig config;
}


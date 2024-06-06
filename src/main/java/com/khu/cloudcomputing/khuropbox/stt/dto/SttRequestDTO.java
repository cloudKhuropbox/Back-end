package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class SttRequestDTO {
    private Mono<byte[]> file;
    private SttRequestConfig config;
}


package com.khu.cloudcomputing.khuropbox.apiPayload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponseDTO {
    private String errorCode;
    private String message;

    public ErrorResponseDTO(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

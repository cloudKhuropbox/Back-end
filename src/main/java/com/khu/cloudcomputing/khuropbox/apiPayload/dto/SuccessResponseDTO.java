package com.khu.cloudcomputing.khuropbox.apiPayload.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class SuccessResponseDTO<T> {
    private T data;

    public SuccessResponseDTO(T data) {
        this.data = data;
    }
}

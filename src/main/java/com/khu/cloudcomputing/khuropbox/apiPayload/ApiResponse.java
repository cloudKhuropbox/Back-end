package com.khu.cloudcomputing.khuropbox.apiPayload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T result;

    public ApiResponse(SuccessStatus status, T result) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.result = result != null ? result : (T) createEmptyJson();
    }

    public ApiResponse(SuccessStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.result = (T) createEmptyJson();
    }

    public ApiResponse(HttpStatus httpStatus, String message, T result) {
        this.code = String.valueOf(httpStatus.value());
        this.message = message;
        this.result = result != null ? result : (T) createEmptyJson();
    }

    private String createEmptyJson() {
        try {
            return new ObjectMapper().writeValueAsString(new Object());
        } catch (Exception e) {
            return "{}";
        }
    }
}
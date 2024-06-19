package com.khu.cloudcomputing.khuropbox.apiPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public GeneralException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}

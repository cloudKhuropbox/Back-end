package com.khu.cloudcomputing.khuropbox.apiPayload;

import com.khu.cloudcomputing.khuropbox.apiPayload.dto.ErrorResponseDTO;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(GeneralException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._UNAUTHORIZED_ACCESS.getCode(), "You do not have permission to perform this action");
        return new ResponseEntity<>(errorResponse, ErrorStatus._UNAUTHORIZED_ACCESS.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllUncaughtException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}

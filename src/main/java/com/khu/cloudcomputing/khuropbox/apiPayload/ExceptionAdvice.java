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

    /**
     * 커스텀 GeneralException 처리
     *
     * @param ex 발생한 GeneralException
     * @return 오류 응답 DTO와 관련 HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(GeneralException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * @Valid 어노테이션이 붙은 인자의 유효성 검사 실패 시 발생하는 MethodArgumentNotValidException 처리
     *
     * @param ex 발생한 MethodArgumentNotValidException
     * @return 오류 응답 DTO와 BAD_REQUEST HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    /**
     * 클래스나 메서드의 유효성 검사 실패 시 발생하는 ConstraintViolationException 처리
     *
     * @param ex 발생한 ConstraintViolationException
     * @return 오류 응답 DTO와 BAD_REQUEST HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    /**
     * HTTP 파라미터를 객체에 바인딩할 때 발생하는 BindException 처리
     *
     * @param ex 발생한 BindException
     * @return 오류 응답 DTO와 BAD_REQUEST HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._BAD_REQUEST.getCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._BAD_REQUEST.getHttpStatus());
    }

    /**
     * 사용자가 권한이 없는 리소스에 접근할 때 발생하는 AccessDeniedException 처리
     *
     * @param ex 발생한 AccessDeniedException
     * @return 오류 응답 DTO와 UNAUTHORIZED_ACCESS HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._UNAUTHORIZED_ACCESS.getCode(), "You do not have permission to perform this action");
        return new ResponseEntity<>(errorResponse, ErrorStatus._UNAUTHORIZED_ACCESS.getHttpStatus());
    }

    /**
     * 모든 잡히지 않은 예외를 처리하여 애플리케이션이 크래시하는 것을 방지하고 일반적인 오류 메시지를 제공
     *
     * @param ex 발생한 Exception
     * @return 오류 응답 DTO와 INTERNAL_SERVER_ERROR HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllUncaughtException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
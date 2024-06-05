package com.khu.cloudcomputing.khuropbox.apiPayload.status;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    _FILE_NOT_FOUND("E001", "File not found", HttpStatus.NOT_FOUND),
    _FILE_UPLOAD_FAILED("E006", "File upload failed", HttpStatus.BAD_REQUEST),
    _FILE_DELETE_FAILED("E007", "File delete failed", HttpStatus.INTERNAL_SERVER_ERROR),

    _INVALID_FILE_FORMAT("E008", "Invalid file format", HttpStatus.BAD_REQUEST),

    _FILE_HISTORY_NOT_FOUND("E009", "File history not found", HttpStatus.NOT_FOUND),

    _UNAUTHORIZED_ACCESS("E002", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    _USER_NOT_FOUND("E005", "User not found", HttpStatus.NOT_FOUND),

    _BAD_REQUEST("E003", "Bad request", HttpStatus.BAD_REQUEST),
    _INTERNAL_SERVER_ERROR("E004", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    _COMMENT_NOT_FOUND("E010", "Comment not found", HttpStatus.NOT_FOUND),
    _COMMENT_CREATION_FAILED("E011", "Comment creation failed", HttpStatus.INTERNAL_SERVER_ERROR),

    _TEAM_NOT_FOUND("E012", "Team not found", HttpStatus.NOT_FOUND),
    _USER_ALREADY_IN_TEAM("E013", "User already in team", HttpStatus.BAD_REQUEST),

    _UNSUPPORTED_MEDIA_TYPE("E014", "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}

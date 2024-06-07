package com.khu.cloudcomputing.khuropbox.apiPayload.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "COMMON200", "Success"),
    _USER_CREATED(HttpStatus.CREATED, "USER201", "User created successfully"),
    _USER_UPDATED(HttpStatus.OK, "USER200", "User updated successfully"),
    _USER_DELETED(HttpStatus.OK, "USER200", "User deleted successfully"),
    _FILE_UPLOADED(HttpStatus.CREATED, "FILE201", "File uploaded successfully"),
    _FILE_DELETED(HttpStatus.OK, "FILE200", "File deleted successfully"),
    _FILE_UPDATED(HttpStatus.OK, "FILE200", "File updated successfully"),
    _COMMENT_CREATED(HttpStatus.CREATED, "COMMENT201", "Comment created successfully"),
    _COMMENT_UPDATED(HttpStatus.OK, "COMMENT200", "Comment updated successfully"),
    _COMMENT_DELETED(HttpStatus.OK, "COMMENT200", "Comment deleted successfully"),
    _TEAM_CREATED(HttpStatus.CREATED, "TEAM201", "Team created successfully"),
    _TEAM_JOINED(HttpStatus.OK, "TEAM200", "Team joined successfully"),
    _TEAM_MEMBER_DELETED(HttpStatus.OK, "TEAM200", "Team member deleted successfully"),
    _TRANSCRIBE_REQUEST_SUCCESS(HttpStatus.OK, "TRANSCRIBE200", "Transcription request successful"),
    _TRANSCRIBE_POLLING_SUCCESS(HttpStatus.OK, "TRANSCRIBE200", "Transcription polling successful");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

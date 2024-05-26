package com.khu.cloudcomputing.khuropbox.apiPayload.status;

import com.khu.cloudcomputing.khuropbox.apiPayload.dto.SuccessResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus{

    // 가장 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공"),

    // User 관련 응답
    _USER_CREATED(HttpStatus.CREATED, "USER201", "사용자 생성 성공"),
    _USER_UPDATED(HttpStatus.OK, "USER200", "사용자 정보 수정 성공"),
    _USER_DELETED(HttpStatus.OK, "USER200", "사용자 삭제 성공"),

    // Files 관련 응답
    _FILE_UPLOADED(HttpStatus.CREATED, "FILE201", "파일 업로드 성공"),
    _FILE_DELETED(HttpStatus.OK, "FILE200", "파일 삭제 성공"),
    _FILE_UPDATED(HttpStatus.OK, "FILE200", "파일 수정 성공"),

    // Comment 관련 응답
    _COMMENT_CREATED(HttpStatus.CREATED, "COMMENT201", "댓글 생성 성공"),
    _COMMENT_UPDATED(HttpStatus.OK, "COMMENT200", "댓글 수정 성공"),
    _COMMENT_DELETED(HttpStatus.OK, "COMMENT200", "댓글 삭제 성공"),

    // Team 관련 응답
    _TEAM_CREATED(HttpStatus.CREATED, "TEAM201", "팀 생성 성공"),
    _TEAM_JOINED(HttpStatus.OK, "TEAM200", "팀 가입 성공"),
    _TEAM_MEMBER_DELETED(HttpStatus.OK, "TEAM200", "팀 멤버 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

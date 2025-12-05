package com.univus.project.constant;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 공통 / 입력 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    DUPLICATE_TEAM_NAME(HttpStatus.CONFLICT, "이미 존재하는 팀 이름입니다."),

    // 팀
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.FORBIDDEN, "해당 팀 구성원이 아닙니다."),

    // 프로젝트
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
    INVALID_RELATION(HttpStatus.BAD_REQUEST, "요청 경로의 연관 관계가 올바르지 않습니다."),
    DUPLICATE_BOARD_NAME(HttpStatus.CONFLICT, "이미 존재하는 프로젝트 이름입니다."),

    // 리포트
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "리포트를 찾을 수 없습니다."),

    // 공지사항
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
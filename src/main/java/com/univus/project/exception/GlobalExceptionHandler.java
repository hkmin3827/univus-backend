package com.univus.project.exception;

import com.univus.project.constant.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 로그인 관련 (이메일 없음, 비밀번호 틀림 등)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        ErrorResponse body = new ErrorResponse(e.getMessage());
        // 원하는 대로 상태코드 지정 (예: 400, 401, 404 중 선택)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 공통 에러 응답 객체
    static class ErrorResponse {
        private final String message;
        private final String code;
        private final int status;

        public ErrorResponse(String message) {
            this.message = message;
            this.code = "AUTH_ERROR";
            this.status = HttpStatus.UNAUTHORIZED.value();
        }

        public ErrorResponse(ErrorCode errorCode) {
            this.message = errorCode.getMessage();
            this.code = errorCode.name();
            this.status = errorCode.getStatus().value();
        }


        public String getMessage() {
            return message;
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }
}

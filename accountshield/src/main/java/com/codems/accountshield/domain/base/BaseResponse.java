package com.codems.accountshield.domain.base;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private ErrorData error;
    private LocalDateTime timestamp;
    private HttpStatus status;

    public static <T> BaseResponse<T> success(T data, HttpStatus status) {
        return success(data, status, null);
    }

    public static <T> BaseResponse<T> success(T data, HttpStatus status, String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data) {
        return success(data, HttpStatus.OK);
    }

    public static <T> BaseResponse<T> success() {
        return success(null);
    }

    public static <T> BaseResponse<T> error(String code, String message, HttpStatus status) {
        return error(code, message, status, null);
    }

    public static <T> BaseResponse<T> error(String code, String message, HttpStatus status, Map<String, String> fieldErrors) {
        return BaseResponse.<T>builder()
                .success(false)
                .error(new ErrorData(code, message, fieldErrors))
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorData {
        private String code;
        private String message;
        private Map<String, String> fieldErrors;
    }
}

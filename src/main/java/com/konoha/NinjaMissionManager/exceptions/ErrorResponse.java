package com.konoha.NinjaMissionManager.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String path;
}
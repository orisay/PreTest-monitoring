package com.tera.pretest.core.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String message;

    public static ResponseEntity<ErrorResponse> errorResponse(){
        return null;
    }
}

package com.tera.pretest.core.exception.restful;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final CustomExceptionCode customExceptionCode;

    @Override
    public String getMessage() {
        return customExceptionCode.getMessage();
    }
}

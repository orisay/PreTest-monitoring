package com.tera.pretest.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final CustomExceptionCode customExceptionCode;
}

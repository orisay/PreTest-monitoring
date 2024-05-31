package com.tera.pretest.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionCode {
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다." );


    private final HttpStatus code;
    private final String message;

}

package com.tera.pretest.core.exception;

import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.exception.restful.CustomException;
import com.tera.pretest.core.exception.restful.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(ex.getCustomExceptionCode().name(), ex.getMessage());
        log.error("CustomException 발생 Code:{}, Message:{}",
                ex.getCustomExceptionCode().name(), ex.getCustomExceptionCode().getMessage(), ex);
        return ResponseEntity.status(ex.getCustomExceptionCode().getCode()).body(errorResponse);
    }

    @ExceptionHandler(ProcessCustomException.class)
    public void handleProcessCustomException(ProcessCustomException ex) {
        log.error("ProcessCustomException 발생 Code:{}, Message:{}",
                ex.getProcessCustomExceptionCode().name(), ex.getProcessCustomExceptionCode().getMessage(), ex);

    }

    @ExceptionHandler(Exception.class)
    public void handleUnexpectedException(Exception ex){
        log.error("가정하지 않은 에러 발생", ex);
    }

}

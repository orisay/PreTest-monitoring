package com.tera.pretest.core.exception.process;


import lombok.Getter;

@Getter
public class ProcessCustomException extends RuntimeException {

    private final ProcessCustomExceptionCode processCustomExceptionCode;

    public ProcessCustomException(ProcessCustomExceptionCode processCustomExceptionCode) {
        super(processCustomExceptionCode.getMessage());
        this.processCustomExceptionCode = processCustomExceptionCode;
    }

    public ProcessCustomException(String message, Throwable cause, ProcessCustomExceptionCode processCustomExceptionCode) {
        super(processCustomExceptionCode.getMessage(), cause);
        this.processCustomExceptionCode = processCustomExceptionCode;
    }



}

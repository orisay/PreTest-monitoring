package com.tera.pretest.core.exception.process;

public enum ProcessCustomExceptionCode {
    SAVE_FAILED("데이터베이스에 저장하는 중에 에러가 발생했습니다."),
    NOT_FOUND_DATA("데이터베이스에 저장된 데이터를 찾을 수 없습니다."),
    UNEXPECTED_ERROR("에상치 못한 에러가 발생했습니다.");


    private final String message;
    ProcessCustomExceptionCode(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}

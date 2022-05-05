package com.womakerscode.microservicemeetup.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}

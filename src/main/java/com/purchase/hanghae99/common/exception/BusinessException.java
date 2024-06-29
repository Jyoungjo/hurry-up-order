package com.purchase.hanghae99.common.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ExceptionCode exceptionCode;
    private List<ErrorResponse.FieldError> errors = new ArrayList<>();

    public BusinessException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public BusinessException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public BusinessException(ExceptionCode exceptionCode, List<ErrorResponse.FieldError> errors) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        this.errors = errors;
    }
}

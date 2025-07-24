package com.common.web.exception;

import com.common.core.exception.ExceptionCode;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String message;
    private List<FieldError> errors;

    private ErrorResponse(final int status, final String message, final List<FieldError> errors) {
        this.status = status;
        this.message = message;
        this.errors = Collections.unmodifiableList(errors);
    }

    private ErrorResponse(final ExceptionCode code, final List<FieldError> errors) {
        this.status = code.getStatus();
        this.message = code.getMessage();
        this.errors = Collections.unmodifiableList(errors);
    }

    private ErrorResponse(final ExceptionCode code) {
        this(code, Collections.emptyList());
    }

    private ErrorResponse(final int status, final String message) {
        this(status, message, Collections.emptyList());
    }

    // Static factory methods
    public static ErrorResponse of(final ExceptionCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ExceptionCode code, final String field, final String value, final String reason) {
        return new ErrorResponse(code, List.of(new FieldError(field, value, reason)));
    }

    public static ErrorResponse of(final ExceptionCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final ExceptionCode code, final Set<ConstraintViolation<?>> constraintViolations) {
        return new ErrorResponse(code, FieldError.of(constraintViolations));
    }

    public static ErrorResponse of(final MethodArgumentTypeMismatchException e) {
        String value = e.getValue() == null ? "" : e.getValue().toString();
        return new ErrorResponse(ExceptionCode.INVALID_TYPE_VALUE,
                List.of(new FieldError(e.getName(), value, e.getMessage())));
    }

    public static ErrorResponse of(final ExceptionCode code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }

    public static ErrorResponse of(final int status, final String message) {
        return new ErrorResponse(status, message);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        public FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }

        public static List<FieldError> of(final Set<ConstraintViolation<?>> violations) {
            return violations.stream()
                    .map(v -> new FieldError(
                            v.getPropertyPath().toString(),
                            "",
                            v.getMessage()))
                    .collect(Collectors.toList());
        }
    }
}
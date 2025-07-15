package com.common.core.constant;

// 상수 모음
public interface HttpStatusCode {
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int NOT_ALLOW_METHOD = 405;
    int CONFLICT = 409;
    int SERVER_ERROR = 500;
}
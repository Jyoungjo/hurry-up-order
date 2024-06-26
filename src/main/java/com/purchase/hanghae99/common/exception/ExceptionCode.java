package com.purchase.hanghae99.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    /*
     * 400
     */
    INVALID_TYPE_VALUE(BAD_REQUEST.value(), "유효성이 일치하지 않음."),
    BAD_CREDENTIALS(BAD_REQUEST.value(), "bad credentials"),
    INVALID_INPUT_VALUE(BAD_REQUEST.value(), "invalid input type"),
    ENCODING_ERROR(BAD_REQUEST.value(), "encoding error"),
    DECODING_ERROR(BAD_REQUEST.value(), "decoding error"),

    /*
     * 401
     */
    UNCERTIFIED_EMAIL(UNAUTHORIZED.value(), "이메일 인증을 진행해 주세요."),
    INVALID_PASSWORD(UNAUTHORIZED.value(), "비밀번호가 일치하지 않습니다."),
    EXPIRED_JWT(UNAUTHORIZED.value(), "만료된 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    UNSUPPORTED_JWT(UNAUTHORIZED.value(), "지원되지 않는 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    INVALID_JWT(UNAUTHORIZED.value(), "유효하지 않은 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    ILLEGAL_ARGUMENT_JWT(UNAUTHORIZED.value(), "잘못된 토큰입니다. 다시 로그인 해주시기 바랍니다."),

    /*
     * 403
     */
    UNAUTHORIZED_ACCESS(FORBIDDEN.value(), "잘못된 접근입니다."),
    CANNOT_LIKE_YOUR_ARTICLE(FORBIDDEN.value(), "자신의 글은 좋아요를 누를 수 없습니다."),
    CANNOT_BOOKMARK_YOUR_ARTICLE(FORBIDDEN.value(), "자신의 글은 북마크할 수 없습니다."),
    CANNOT_FOLLOW_YOURSELF(FORBIDDEN.value(), "자기 자신은 팔로우 할 수 없습니다."),

    /*
     * 404
     */
    NOT_FOUND_USER(NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    NOT_FOUND_ITEM(NOT_FOUND.value(), "존재하지 않는 물품입니다."),
    NOT_FOUND_ORDER(NOT_FOUND.value(), "존재하지 않는 주문입니다."),
    NOT_FOUND_WISHLIST(NOT_FOUND.value(), "존재하지 않는 위시 리스트입니다."),
    NOT_FOUND_WISHLIST_ITEM(NOT_FOUND.value(), "위시 리스트에 존재하지 않는 물품입니다."),
    NOT_FOUND_CART(NOT_FOUND.value(), "존재하지 않는 장바구니입니다."),
    NOT_FOUND_CART_ITEM(NOT_FOUND.value(), "장바구니에 존재하지 않는 물품입니다."),

    /*
     * 405
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "method not allowed"),

    /*
     * 409
     */
    ALREADY_REGISTERED_EMAIL(CONFLICT.value(), "already registered email"),
    ALREADY_REGISTERED_PHONE_NUMBER(CONFLICT.value(), "already registered phone number"),
    ALREADY_EXISTS_ITEM(CONFLICT.value(), "이미 등록된 아이템 입니다."),

    /*
     * 500
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal server error");

    private final int status;
    private final String message;
}

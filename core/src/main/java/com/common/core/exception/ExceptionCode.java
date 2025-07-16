package com.common.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.common.core.constant.HttpStatusCode.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    /*
     * 400
     */
    INVALID_TYPE_VALUE(BAD_REQUEST, "유효성이 일치하지 않음."),
    BAD_CREDENTIALS(BAD_REQUEST, "bad credentials"),
    INVALID_INPUT_VALUE(BAD_REQUEST, "입력값을 확인해주세요."),
    ENCODING_ERROR(BAD_REQUEST, "encoding error"),
    DECODING_ERROR(BAD_REQUEST, "decoding error"),
    CANCEL_PAYMENT(BAD_REQUEST, "결제가 취소되었습니다."),
    NOT_REACHED_OPEN_TIME(BAD_REQUEST, "결제가 취소되었습니다."),

    /*
     * 401
     */
    UNCERTIFIED_EMAIL(UNAUTHORIZED, "이메일 인증을 진행해 주세요."),
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    EXPIRED_JWT(UNAUTHORIZED, "만료된 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    UNSUPPORTED_JWT(UNAUTHORIZED, "지원되지 않는 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    INVALID_JWT(UNAUTHORIZED, "유효하지 않은 토큰입니다. 다시 로그인 해주시기 바랍니다."),
    ILLEGAL_ARGUMENT_JWT(UNAUTHORIZED, "잘못된 토큰입니다. 다시 로그인 해주시기 바랍니다."),

    /*
     * 403
     */
    UNAUTHORIZED_ACCESS(FORBIDDEN, "잘못된 접근입니다."),
    CANNOT_LIKE_YOUR_ARTICLE(FORBIDDEN, "자신의 글은 좋아요를 누를 수 없습니다."),
    CANNOT_BOOKMARK_YOUR_ARTICLE(FORBIDDEN, "자신의 글은 북마크할 수 없습니다."),
    CANNOT_FOLLOW_YOURSELF(FORBIDDEN, "자기 자신은 팔로우 할 수 없습니다."),

    /*
     * 404
     */
    NOT_FOUND_USER(NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_FOUND_ITEM(NOT_FOUND, "존재하지 않는 물품입니다."),
    NOT_FOUND_ORDER(NOT_FOUND, "존재하지 않는 주문입니다."),
    NOT_FOUND_ORDER_ITEM(NOT_FOUND, "주문하신 상품이 없습니다."),
    NOT_FOUND_WISHLIST(NOT_FOUND, "존재하지 않는 위시 리스트입니다."),
    NOT_FOUND_WISHLIST_ITEM(NOT_FOUND, "위시 리스트에 존재하지 않는 물품입니다."),
    NOT_FOUND_CART(NOT_FOUND, "존재하지 않는 장바구니입니다."),
    NOT_FOUND_CART_ITEM(NOT_FOUND, "장바구니에 존재하지 않는 물품입니다."),
    NOT_FOUND_STOCK(NOT_FOUND, "재고가 존재하지 않습니다."),
    NOT_FOUND_SHIPMENT(NOT_FOUND, "배송 정보가 존재하지 않습니다."),
    NOT_FOUND_PAYMENT(NOT_FOUND, "결제 정보가 존재하지 않습니다."),

    /*
     * 405
     */
    METHOD_NOT_ALLOWED(NOT_ALLOW_METHOD, "method not allowed"),

    /*
     * 409
     */
    INVALID_VERIFICATION_NUMBER(CONFLICT, "인증번호가 일치하지 않습니다."),
    ALREADY_REGISTERED_EMAIL(CONFLICT, "이미 등록된 이메일 입니다."),
    ALREADY_REGISTERED_PHONE_NUMBER(CONFLICT, "이미 등록된 전화번호 입니다."),
    ALREADY_EXISTS_ITEM(CONFLICT, "이미 등록된 아이템 입니다."),
    ALREADY_SHIPPING(CONFLICT, "이미 배송중인 물품은 취소할 수 없습니다."),
    NO_RETURN(CONFLICT, "이미 배송중인 물품은 취소할 수 없습니다."),
    NOT_ENOUGH_STOCK(CONFLICT, "재고가 충분하지 않습니다."),

    /*
     * 500
     */
    INTERNAL_SERVER_ERROR(SERVER_ERROR, "internal server error");

    private final int status;
    private final String message;
}

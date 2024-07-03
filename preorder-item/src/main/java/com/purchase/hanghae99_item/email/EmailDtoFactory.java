package com.purchase.hanghae99_item.email;

public class EmailDtoFactory {
    private static final ResEmailDto SUCCESS_INSTANCE = ResEmailDto.builder()
            .status(true)
            .message("인증에 성공하셨습니다.")
            .build();

    private static final ResEmailDto FAILURE_INSTANCE = ResEmailDto.builder()
            .status(false)
            .message("인증에 실패하셨습니다. 다시 시도해주시기 바랍니다.")
            .build();

    public static ResEmailDto succeed() {
        return SUCCESS_INSTANCE;
    }

    public static ResEmailDto fail() {
        return FAILURE_INSTANCE;
    }
}

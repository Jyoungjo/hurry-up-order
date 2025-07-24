package com.common.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentPGName {
    TOSS("tvivarepublica"), NICE("nicepay");

    private final String name;
}

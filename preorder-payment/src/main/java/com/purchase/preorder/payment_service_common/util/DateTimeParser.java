package com.purchase.preorder.payment_service_common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class DateTimeParser {

    public static LocalDateTime parse(String date) {
        if (date.length() == 28) date = date.substring(0, 26) + ":" + date.substring(26);
        return OffsetDateTime.parse(date).toLocalDateTime();
    }
}

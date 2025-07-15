package com.common.core.util;

public class RedisKeyHelper {
    private static final String ITEM = "item";
    private static final String STOCK = "stock";
    private static final String EMAIL = "email";

    public static String itemKey(Long itemId) {
        return ITEM + "::" + itemId;
    }

    public static String stockKey(Long itemId) {
        return STOCK + "::" + itemId;
    }

    public static String emailKey(String email) {
        return EMAIL + "::" + email;
    }
}
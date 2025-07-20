package com.common.core.util;

public class RedisKeyHelper {
    private static final String ITEM = "item";
    private static final String STOCK = "stock";
    private static final String EMAIL = "email";
    private static final String TOSS = "toss:confirm";
    private static final String NICE = "nice:confirm";

    public static String itemKey(Long itemId) {
        return ITEM + "::" + itemId;
    }

    public static String stockKey(Long itemId) {
        return STOCK + "::" + itemId;
    }

    public static String emailKey(String email) {
        return EMAIL + "::" + email;
    }

    public static String tossKey(String pgOrderId) {
        return TOSS + "::" + pgOrderId;
    }

    public static String niceKey(String pgOrderId) {
        return NICE + "::" + pgOrderId;
    }
}
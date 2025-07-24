package com.purchase.preorder.payment_service_common.util;

import java.util.Random;

public class RandomUtils {
    private static Random random;
    private final static String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*-_";

    private RandomUtils() {}

    public static String makeRandomString(int initSize, int maxSize) {
        random = new Random();
        int size = random.nextInt(maxSize) + initSize;
        return execute(size);
    }

    private static String execute(int size) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < size) {
            sb.append(str.charAt(random.nextInt(str.length() - 1)));
        }
        return sb.toString();
    }
}

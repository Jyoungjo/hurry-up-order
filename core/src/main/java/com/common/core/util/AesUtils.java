package com.common.core.util;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class AesUtils {
    @Getter
    private static String privateKey;

    @Value("${aes.privateKey}")
    public void setPrivateKey(String privateKey) {
        AesUtils.privateKey = privateKey;
    }

    public static String aesCBCEncode(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(getPrivateKey().getBytes(UTF_8), "AES");
        // TODO: IV 랜덤성 부여 -> 우선 편의를 위해 고정값 지정
        IvParameterSpec Iv = new IvParameterSpec(getPrivateKey().substring(0, 16).getBytes());
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKey, Iv);
        byte[] encodedByte = c.doFinal(plainText.getBytes(UTF_8));
        return Hex.encodeHexString(encodedByte);
    }

    public static String aesCBCDecode(String encodeText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(getPrivateKey().getBytes(UTF_8), "AES");
        IvParameterSpec Iv = new IvParameterSpec(getPrivateKey().substring(0, 16).getBytes());
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secretKey, Iv);
        byte[] decodedByte = Hex.decodeHex(encodeText);
        return new String(c.doFinal(decodedByte), UTF_8);
    }
}
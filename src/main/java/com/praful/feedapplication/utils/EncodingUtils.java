package com.praful.feedapplication.utils;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class EncodingUtils {
    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decodeBase64(String input) {
        return new String(Base64.getDecoder().decode(input));
    }

    private EncodingUtils() {
    }
}

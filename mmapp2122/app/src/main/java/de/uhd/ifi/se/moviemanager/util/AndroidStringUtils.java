package de.uhd.ifi.se.moviemanager.util;

import java.security.SecureRandom;
import java.util.function.Predicate;

public final class AndroidStringUtils {
    public static final int IDENTIFIER_LENGTH = 64;

    // private constructor to prevent instantiation
    private AndroidStringUtils() {
        throw new UnsupportedOperationException();
    }

    public static String generateIdentifier(Predicate<String> condition) {
        String key = generateIdentifier();
        while (condition.test(key)) {
            key = generateIdentifier();
        }
        return key;
    }

    private static String generateIdentifier() {
        byte[] code = new byte[IDENTIFIER_LENGTH];
        char[] chars = new char[code.length];

        new SecureRandom().nextBytes(code);
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) code[i];
        }

        return new String(chars);
    }
}

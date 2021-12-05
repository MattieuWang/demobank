package com.junzhe.demobank.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.UUID;

public class StringUtil {

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String encryptPW(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    public static boolean isPwCorrect(String pw, String hash) {
        return BCrypt.checkpw(pw, hash);
    }

}

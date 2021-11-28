package com.junzhe.demobank.utils;

import com.junzhe.demobank.models.JwtUser;
import com.junzhe.demobank.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Date;
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

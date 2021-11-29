package com.junzhe.demobank.utils;

import com.junzhe.demobank.models.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class CookieUtils {


    private static final  String APPSECRET = "secret";
    public static final long EXPIRE = 1000*2*60;  // 2 mins

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isEmpty(name)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public static void addCookie(String name, String value, String path, HttpServletRequest request,
                                 HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        if (path != null) {
            cookie.setPath(path);
        }
        if ("https".equals(request.getScheme())) {
            cookie.setSecure(true);
        }
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void removeCookie(String name, String path, HttpServletResponse response) {

        Cookie cookie = new Cookie(name, null);

        if (path != null) {
            cookie.setPath(path);
        }
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


    public static Claims getSessionClaim (String session) {
        return checkJWT(session);
    }

    public static Claims checkJWT(String token){

        try{
            return Jwts.parser().setSigningKey(APPSECRET).
                    parseClaimsJws(token).getBody();

        } catch (Exception ignored){ }
        return null;

    }

    public static void refreshToken(String session) {
        Claims claims = getSessionClaim(session);
        if (claims != null) {
            claims.setExpiration(new Date(System.currentTimeMillis()+EXPIRE));
        }
    }

    public static String generateJWT(JwtUser user){

        return Jwts.builder().setSubject(user.getUsername())
                .claim("id",user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRE))
                .signWith(SignatureAlgorithm.HS256,APPSECRET).compact();
    }

    public static boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }
}

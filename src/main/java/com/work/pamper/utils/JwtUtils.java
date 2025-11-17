package com.work.pamper.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    // 生成用于签名和验证的密钥
    // HS256对称加密算法
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 生成密钥

    /**
     * @param subject JWT的主题，可以是用户ID或其他标识信息
     * @param ttlMillis JWT的有效期，单位为毫秒 比如3600000表示1小时
     * @return 生成的JWT字符串
     */
    // 创建JWT
    public static String createJwt(String subject, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 设置JWT的过期时间
        Date exp = new Date(nowMillis + ttlMillis);

        // 构建JWT
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * @param jwt 要解析的JWT字符串
     * @return 解析后的Claims对象，包含JWT中的信息
     */
    // 解析JWT
    public static Claims parseJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    // 验证JWT并提取信息
    public static String getSubject(String jwt) {
        Claims claims = parseJwt(jwt);
        return claims.getSubject();
    }

    // 检查JWT是否过期
    public static boolean isExpired(String jwt) {
        Claims claims = parseJwt(jwt);
        return claims.getExpiration().before(new Date());
    }
}

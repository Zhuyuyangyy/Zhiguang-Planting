package com.laserdentalrobot.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT工具类 生成和解析token
 */
@Component
public class JwtUtil {

    // 密钥自动生成的不用管
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成token 存userId和role
    public String generateToken(Long userId, Integer role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24小时过期
                .signWith(SECRET_KEY)
                .compact();
    }


    // 从token拿用户id（必须加！否则其他地方调用报错）
    public Long getUserIdFromToken(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    // 从token拿角色
    public Integer getRoleFromToken(String token) {
        return extractClaim(token, claims -> claims.get("role", Integer.class));
    }

    // 通用解析方法
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}

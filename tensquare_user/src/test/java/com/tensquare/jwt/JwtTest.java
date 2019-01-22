package com.tensquare.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;

public class JwtTest {

    @Test
    public void crateToken() {
        String token = Jwts.builder().setId("123456")
                .setSubject("江小白")
                .setIssuedAt(new Date())
                //参数1：签名算法， 参数2：密钥
                .signWith(SignatureAlgorithm.HS256, "itcast")
                //生成token
                .compact();
        System.out.println(token);
    }

    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjM0NTYiLCJzdWIiOiLkvKDmmbrmkq3lrqIiLCJpYXQiOjE1NDY3NDM1NzksImV4cCI6MTU0Njc0MzYzOX0.TCwkJjAz-hT6ZN3oREx6pdE9xa0d6h-8G9vzBSkA2Ks";
        Claims claims = Jwts.parser()
                //设置密钥
                .setSigningKey("itcast")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims.getId());
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt().toLocaleString());
    }

    @Test
    public void createTokenWithExp() {
        long currentTimeMillis = System.currentTimeMillis();
        long expTime = currentTimeMillis + (60 * 1000);
        String token = Jwts.builder().setId("123456")
                .setSubject("传智播客")
                //签发时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(new Date(expTime))
                .signWith(SignatureAlgorithm.HS256, "itcast")
                .compact();
        System.out.println(token);

    }

    @Test
    public void createTokenWithClaim() {
        String token = Jwts.builder().setId("123456")
                .setSubject("测试的token")
                .setIssuedAt(new Date())
                //设置自定义字段
                .claim("test", "hello")
                .signWith(SignatureAlgorithm.HS256, "itcast").compact();
        System.out.println(token);
        Claims claims = Jwts.parser().setSigningKey("itcast")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims.getId());
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt());
        System.out.println(claims.get("test"));

    }


}

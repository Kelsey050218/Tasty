package com.cs183.tasty.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    //生成jwt令牌
    public static String createJWT(String signKey,long expire,Map<String, Object> claims){
        String jwt = Jwts.builder()
                .setClaims(claims)//私有声明可覆盖标准声明，不用addClaims方法
                .signWith(SignatureAlgorithm.HS256, signKey.getBytes(StandardCharsets.UTF_8))//签名算法（头部）
                .setExpiration(new Date(System.currentTimeMillis() + expire))//过期时间(毫秒）
                .compact();
        return jwt;
    }
    //解析令牌
    public static Claims parseJWT(String signKey,String jwt){
        Claims claims = Jwts.parser()
                .setSigningKey(signKey.getBytes(StandardCharsets.UTF_8))//指定签名密钥
                .parseClaimsJws(jwt)//指定需要被解析的令牌Token
                .getBody();
        return claims;
    }



}

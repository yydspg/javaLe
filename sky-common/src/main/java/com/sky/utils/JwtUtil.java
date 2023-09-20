package com.sky.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil{

    /**
     *生成jwt
     * use H256
     * @param secretKey jwt密钥
     * @param ttlMillis
     * @param claims
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String,Object> claims){
        //header
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //指定过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        //payload
        JwtBuilder builder = Jwts.builder()
                //调用setClaims 会将之前set 的 claims清除
                .setClaims(claims)
                //设置签名算法和使用的密钥
                .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
                //设置expiration
                .setExpiration(exp);
        return builder.compact();

    }

    /**
     * Token解密
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token 加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey,String token){
        //get DefaultJwtParser
        Claims claims = Jwts.parser()
                // set sign secretKey
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
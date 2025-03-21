package com.zqq.common.core.utils;

import com.zqq.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;


public class JwtUtils {

    /**
     * 生成令牌
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims,String secret){
        String token=
                Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512,secret).compact();
        return token;
    }


    /**
     * 从令牌中获取数据
     * @param token 令牌
     * @param secret 密钥
     * @return  数据
     */
    public static Claims parseToken(String token,String secret){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }


    public static String getUserKey(Claims claims) {
        return getString(claims.get(JwtConstants.LOGIN_USER_KEY));
    }

    public static String getUserId(Claims claims) {
        return getString(claims.get(JwtConstants.LOGIN_USER_ID));

    }

    private static String getString(Object value) {
        if(value ==null){
            return "";
        }

        return value.toString();
    }

    public static void main(String[] args) {
        Map<String,Object> claims=new HashMap<>();
        claims.put("userId",123456789L);
//        secret 保密 随机性 不能硬编码（不能写在源代码中）需要定期更换


//        System.out.println(createToken(claims, "aaaaaaaaaaaaaaaaaaaaaaaa"));
        String token="eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEyMzQ1Njc4OX0.Ij37QS3BU3pmDIbxNmhNacB3eV6gdpZgJWqu2wuMsjIe7rrZfXMZviX2yXOud3qBtP8t5foc6kpQPkKHEDxe-w";
        String security="aaaaaaaaaaaaaaaaaaaaaaaa";

        Claims claims1 = parseToken(token, security);
        System.out.println(claims1);

//        1，用户登入成功之后，调用create方法生成令牌，并发送给客户端
//        2，后续所有请求，在调用具体的接口之前，都要先通过token进行身份认证


//        3，用户使用系统的过程中，需要进行合适的延长jwt的过期时间



    }



}

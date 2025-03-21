package com.zqq.common.security.service;

import cn.hutool.core.lang.UUID;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.constants.JwtConstants;
import com.zqq.common.core.domain.LoginUser;
import com.zqq.common.core.utils.JwtUtils;
import com.zqq.redis.service.RedisService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 操作用户登入token的方法
 */
@Service
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId,String secret,Integer identity){
        Map<String,Object> claims=new HashMap<>();
        String userKey = UUID.fastUUID().toString();
        claims.put(JwtConstants.LOGIN_USER_ID,userId);
        claims.put(JwtConstants.LOGIN_USER_KEY,userKey);
        String token = JwtUtils.createToken(claims, secret);
//            1,第三方机制中存在敏感信息 表明用户身份字段： identity 1表示普通用户，2表示管理员用户 对象
//            2,使用什么样子的数据结构 key:value(:String   String   hash       (String)可以直接使用序列化器
//            key要保证唯一， 便于维护。统一前缀:loginToken:(userId 通过雪花算法生成的唯一的)

        String key=getTokenKsy(userKey);
        LoginUser loginUser=new LoginUser();
        loginUser.setIdentity(identity);
        redisService.setCacheObject(key,loginUser,CacheConstants.EXP, TimeUnit.MINUTES);
//            3,过期时间应该怎么定义  720分钟

        return token;
    }

//    在身份认证通过并且在请求到达controller之前进行判别延长
    /**
     * 延长token的有效时间就是延长redis中的信息        需要操作redis   token-> 唯一标识
     * @param token
     */
    public void extendToken(String token,String secret){

        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret); //获取令牌中信息  解析payload中信息  存储着用户唯一标识信息
            if (claims == null) {
//                TODO

            }
        } catch (Exception e) {
//            TODO

        }

        String userKey = JwtUtils.getUserKey(claims);
        String tokenKey=getTokenKsy(userKey);

//        剩余180min分钟延长
        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if(expire!=null&&expire<CacheConstants.REFRESH_TIME){
            redisService.expire(tokenKey,CacheConstants.EXP,TimeUnit.MINUTES);
        }

    }

    private String getTokenKsy(String userKey){
        return CacheConstants.LOGIN_TOKEN_KEY+userKey;
    }

}

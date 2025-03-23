package com.zqq.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器，延长token
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;  //从哪个服务配置文件中获取，取决于这个bean对象交给了哪个服务的spring容器进行管理

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=getToken(request);
        tokenService.extendToken(token,secret);
        return true;
    }


    private String getToken(HttpServletRequest request){
        String token= request.getHeader(HttpConstants.AUTHENTICATION);
        if(StrUtil.isNotEmpty(token)&&token.startsWith(HttpConstants.PREFIX)){
            token=token.replaceFirst(HttpConstants.PREFIX,"");
        }
        return token;
    }
}

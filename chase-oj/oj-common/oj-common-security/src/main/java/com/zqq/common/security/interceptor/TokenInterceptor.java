package com.zqq.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.common.security.service.TokenService;
import io.jsonwebtoken.Claims;
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

    /**
     * 请求拦截器处理（主要进行token令牌的身份判断）
     * @param request 请求
     * @param response 响应
     * @param handler 处理
     * @return 返回通过还是不通过
     * @throws Exception 抛出异常
     */
    @Override
    public boolean preHandle( HttpServletRequest request,  HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);  //请求头中获取token
        if (StrUtil.isEmpty(token)) {
            return true;
        }
//        解析出载荷信息
        Claims claims = tokenService.getClaims(token, secret);
//        根据载荷信息，设置线程本地变量存储
        Long userId = tokenService.getUserId(claims);
        ThreadLocalIUtil.set(Constants.USER_ID,userId);
//        判断是否需要对token进行延迟处理
        tokenService.extendToken(token,secret);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        用完之后再把线程变量删除，防止占用空间
        ThreadLocalIUtil.remove();
    }

    /**
     * 从请求中获取token
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request){
        String token= request.getHeader(HttpConstants.AUTHENTICATION);
        if(StrUtil.isNotEmpty(token)&&token.startsWith(HttpConstants.PREFIX)){
            token=token.replaceFirst(HttpConstants.PREFIX,"");
        }
        return token;
    }


}

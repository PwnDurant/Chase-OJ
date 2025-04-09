package com.zqq.friend.aspect;


import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.domain.user.vo.UserVO;
import com.zqq.friend.manager.UserCacheManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class UserStatusCheckAspect {

    @Autowired
    private UserCacheManager userCacheManager;

    @Before(value = "@annotation(com.zqq.friend.aspect.CheckUserStatus)")
    public void before (JoinPoint point){
        Long userId= ThreadLocalIUtil.get(Constants.USER_ID, Long.class);
        UserVO userVO=userCacheManager.getUserById(userId);
        if(userVO==null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if(Objects.equals(userVO.getStatus(),Constants.FALSE)){
            throw new ServiceException(ResultCode.FAILED_USER_BANNED);
        }
    }

}

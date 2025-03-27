package com.zqq.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.message.service.AliSmsService;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.domain.dto.UserDTO;
import com.zqq.friend.service.IUserService;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements IUserService {

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private RedisService redisService;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Override
    public void sendCode(UserDTO userDTO) {
        if(!checkPhone(userDTO.getPhone())){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        Long expire = redisService.getExpire(getPhoneCodeKey(userDTO.getPhone()), TimeUnit.SECONDS);
        if(expire!=null&&(phoneCodeExpiration*60-expire)<120){
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }

        String code= RandomUtil.randomNumbers(6);
//        存储到redis中，数据结构：String key：p：c：手机号     value：code
        redisService.setCacheObject(getPhoneCodeKey(userDTO.getPhone()),code,phoneCodeExpiration, TimeUnit.MINUTES);
        aliSmsService.sendMobileCode(userDTO.getPhone(),code);
    }

    private String getPhoneCodeKey(String phone) {
        return Constants.PHONE_CODE_KEY+phone;
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
}

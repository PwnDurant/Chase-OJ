package com.zqq.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.core.domain.LoginUser;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.vo.LoginUserVO;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.enums.UserIdentity;
import com.zqq.common.core.enums.UserStatus;
import com.zqq.common.message.service.AliSmsService;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.common.security.service.TokenService;
import com.zqq.friend.domain.user.User;
import com.zqq.friend.domain.user.dto.UserDTO;
import com.zqq.friend.domain.user.dto.UserUpdateDTO;
import com.zqq.friend.domain.user.vo.UserVO;
import com.zqq.friend.manage.UserCacheManager;
import com.zqq.friend.mapper.user.UserMapper;
import com.zqq.friend.service.user.IUserService;
import com.zqq.redis.service.RedisService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.send-limit:10}")
    private Long sendLimit;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${sms.is-send:false}")
    private boolean isSend;

    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;



    @Override
    public boolean sendCode(UserDTO userDTO) {
        if(!checkPhone(userDTO.getPhone())){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        Long expire = redisService.getExpire(getPhoneCodeKey(userDTO.getPhone()), TimeUnit.SECONDS);
        if(expire!=null&&(phoneCodeExpiration*60-expire)<60){
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }
//        对于每一天的验证获取限制为50次，第二天清零        计数
//        操作这个次数数据频繁，不需要存储，记录的次数 有有效时间（当天有效）redis
//        缓存结构:String key: c:t:手机号
//        如果大于限制就抛出异常，如果不大于限制就正常执行后续逻辑
        Long sendTimes = redisService.getCacheObject(getCodeTimeKey(userDTO.getPhone()), Long.class);
        if(sendTimes!=null&&sendTimes>=sendLimit){
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }

        String code= isSend ? RandomUtil.randomNumbers(6):Constants.DEFAULT_CODE;
//        存储到redis中，数据结构：String key：p：c：手机号     value：code
        redisService.setCacheObject(getPhoneCodeKey(userDTO.getPhone()),code,phoneCodeExpiration, TimeUnit.MINUTES);
        if(isSend){
            boolean sendMobileCode = aliSmsService.sendMobileCode(userDTO.getPhone(), code);
            if(!sendMobileCode){
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        }
//        加一操作
        redisService.increment(getCodeTimeKey(userDTO.getPhone()));

        if(sendTimes==null){
//            说明是当天第一次获取发起请求
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
//            把第一个key设置过期时间
            redisService.expire(getCodeTimeKey(userDTO.getPhone()),seconds,TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public String codeLogin(UserDTO userDTO) {

//      先判断验证码的正确性
        checkCode(userDTO);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, userDTO.getPhone()));

        if(user==null){ //说明是新用户
//            注册逻辑
            user=new User();
            user.setPhone(userDTO.getPhone());
            user.setStatus(UserStatus.Normal.getValue());
            userMapper.insert(user);
        }

//            返回登入/注册成功的token令牌
        return tokenService.createToken(user.getUserId(),secret, UserIdentity.ORDINARY.getValue(), user.getNickName(), user.getHeadImage());

    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token,secret);
    }

    @Override
    public R<LoginUserVO> info(String token) {

        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if(loginUser==null){
            return R.fail();
        }
        LoginUserVO loginUserVO=new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        if (StrUtil.isNotEmpty(loginUser.getHeadImage())) {
            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
        }
        return R.ok(loginUserVO);
    }

    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {
        User user = isExist();
        BeanUtil.copyProperties(userUpdateDTO,user);
        //更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        return userMapper.updateById(user);
    }

    @Override
    public int updateHeadImage(String headImage) {
//        先判断用户的是否存在
        User user = isExist();
//        设置用户头像，上传头像后返回的唯一标识
        user.setHeadImage(headImage);
//        更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));

        return userMapper.updateById(user);
    }

    @NotNull
    private User isExist() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return user;
    }

    private void checkCode(UserDTO userDTO) {
        //            根据手机号拿到存储redis的验证码
        String phoneCodeKey = getPhoneCodeKey(userDTO.getPhone());
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        if(StrUtil.isEmpty(cacheCode)){
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if(!cacheCode.equals(userDTO.getCode())){
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
//            验证码对比成功,先删除在redis中验证码
        redisService.deleteObject(phoneCodeKey);
    }

    private String getPhoneCodeKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY+phone;
    }
    private String getCodeTimeKey(String phone) {
        return CacheConstants.CODE_TIME_KEY+phone;
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
}

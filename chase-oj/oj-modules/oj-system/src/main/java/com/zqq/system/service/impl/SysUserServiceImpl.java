package com.zqq.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.enums.UserIdentity;
import com.zqq.common.security.service.TokenService;
import com.zqq.system.domain.SysUser;
import com.zqq.system.mapper.SysUserMapper;
import com.zqq.system.service.ISysUserService;
import com.zqq.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public R<String> login(String userAccount, String password) {

//        通过账号在数据库中查询对应的用户信息
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
//        select password from tb_sys_user where user_account=#{userAccount};
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getUserId,SysUser::getPassword).eq(SysUser::getUserAccount, userAccount));
        if(sysUser==null){
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        if(BCryptUtils.matchesPassword(password,sysUser.getPassword())){
            return R.ok(tokenService.createToken(sysUser.getUserId(), secret, UserIdentity.ADMIN.getValue()));
        }

        return R.fail(ResultCode.FAILED_LOGIN);
    }

//    编译时异常（受检异常）

//    运行时异常（非受检异常）

}

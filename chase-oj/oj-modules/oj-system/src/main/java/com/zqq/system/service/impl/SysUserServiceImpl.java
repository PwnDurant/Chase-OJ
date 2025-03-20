package com.zqq.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.system.domain.SysUser;
import com.zqq.system.mapper.SysUserMapper;
import com.zqq.system.service.ISysUserService;
import com.zqq.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public R<Void> login(String userAccount, String password) {

//        通过账号在数据库中查询对应的用户信息
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
//        select password from tb_sys_user where user_account=#{userAccount};
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getPassword).eq(SysUser::getUserAccount, userAccount));
        if(sysUser==null){
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        if(BCryptUtils.matchesPassword(password,sysUser.getPassword())){
            return R.ok();
        }

        return R.fail(ResultCode.FAILED_LOGIN);
    }

//    编译时异常（受检异常）

//    运行时异常（非受检异常）

}

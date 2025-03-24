package com.zqq.system.service.sysuser.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.core.domain.LoginUser;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.vo.LoginUserVO;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.enums.UserIdentity;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.common.security.service.TokenService;
import com.zqq.system.domain.sysuser.SysUser;
import com.zqq.system.domain.sysuser.dto.SysUserSaveDTO;
import com.zqq.system.mapper.sysuser.SysUserMapper;
import com.zqq.system.service.sysuser.ISysUserService;
import com.zqq.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

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
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
//        select password from tb_sys_user where user_account=#{userAccount};
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getUserId, SysUser::getPassword,SysUser::getNickName).eq(SysUser::getUserAccount, userAccount));
        if (sysUser == null) {
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
            return R.ok(tokenService.createToken(sysUser.getUserId(), secret, UserIdentity.ADMIN.getValue(), sysUser.getNickName()));
        }

        return R.fail(ResultCode.FAILED_LOGIN);
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
//        先将DTO转为实体

        List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));

        if(CollectionUtil.isNotEmpty(sysUsers)){
//            用户已存在
//            提供一个自定义的异常
            throw new ServiceException(ResultCode.FAILED_USER_EXISTS);
        }

        SysUser sysUser=new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
//        sysUser.setUpdateBy(100L);
//        sysUser.setUpdateTime(LocalDateTime.now());
        return sysUserMapper.insert(sysUser);
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
        return R.ok(loginUserVO);
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token,secret);
    }

//    编译时异常（受检异常）

//    运行时异常（非受检异常）

}

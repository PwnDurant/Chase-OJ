package com.zqq.system.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.system.domain.user.User;
import com.zqq.system.domain.user.dto.UserDTO;
import com.zqq.system.domain.user.dto.UserQueryDTO;
import com.zqq.system.domain.user.vo.UserVO;
import com.zqq.system.manage.UserCacheManager;
import com.zqq.system.mapper.user.UserMapper;
import com.zqq.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager userCacheManager;


    @Override
    public List<UserVO> list(UserQueryDTO userQueryDTO) {
        PageHelper.startPage(userQueryDTO.getPageNum(),userQueryDTO.getPageSize());
        return userMapper.selectUserList(userQueryDTO);
    }

    @Override
    public int updateStatus(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getUserId());
        if(user==null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userDTO.getStatus());
        userCacheManager.updateStatus(user.getUserId(), userDTO.getStatus());
        return userMapper.updateById(user);
    }
}

package com.zqq.system.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.system.domain.user.User;
import com.zqq.system.domain.user.dto.UserQueryDTO;
import com.zqq.system.domain.user.vo.UserVO;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {

    List<UserVO> selectUserList(UserQueryDTO userQueryDTO);
}

package com.zqq.system.service.user;

import com.zqq.system.domain.user.dto.UserDTO;
import com.zqq.system.domain.user.dto.UserQueryDTO;
import com.zqq.system.domain.user.vo.UserVO;

import java.util.List;

public interface IUserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateStatus(UserDTO userDTO);
}

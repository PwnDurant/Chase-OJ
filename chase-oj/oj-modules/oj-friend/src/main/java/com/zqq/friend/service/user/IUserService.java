package com.zqq.friend.service.user;

import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.vo.LoginUserVO;
import com.zqq.friend.domain.user.dto.UserDTO;
import com.zqq.friend.domain.user.dto.UserUpdateDTO;
import com.zqq.friend.domain.user.vo.UserVO;

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(UserDTO userDTO);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    UserVO detail();

    int edit(UserUpdateDTO userUpdateDTO);

    int updateHeadImage(String headImage);
}

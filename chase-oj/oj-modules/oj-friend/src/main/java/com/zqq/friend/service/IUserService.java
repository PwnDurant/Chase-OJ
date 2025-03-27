package com.zqq.friend.service;

import com.zqq.friend.domain.dto.UserDTO;

public interface IUserService {
    void sendCode(UserDTO userDTO);
}

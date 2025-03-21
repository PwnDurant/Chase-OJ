package com.zqq.system.service;

import com.zqq.common.core.domain.R;
import com.zqq.system.controller.LoginResult;

public interface ISysUserService {
    R<String> login(String userAccount, String password);
}

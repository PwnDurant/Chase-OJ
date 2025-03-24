package com.zqq.system.service.sysuser;

import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.vo.LoginUserVO;
import com.zqq.system.domain.sysuser.dto.SysUserSaveDTO;

public interface ISysUserService {
    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(String token);

    boolean logout(String token);
}

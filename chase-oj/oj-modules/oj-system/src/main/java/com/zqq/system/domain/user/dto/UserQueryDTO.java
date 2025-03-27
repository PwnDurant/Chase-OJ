package com.zqq.system.domain.user.dto;

import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserQueryDTO extends PageQueryDTO {

    private Long userId;

    private String nickName;
}

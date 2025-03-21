package com.zqq.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum UserIdentity {

    ORDINARY(1,"普通用户"),
    ADMIN(2,"管理员");

    private final Integer value;
    private final String des;
}

package com.zqq.common.core.enums;


import lombok.Getter;

@Getter
public enum UserStatus {
    Block(0),
    Normal(1);

    private Integer value;

    UserStatus(Integer value) {
        this.value = value;
    }
}

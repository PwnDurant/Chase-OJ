package com.zqq.common.core.enums;


import lombok.Getter;


@Getter
public enum ProgramType {

    JAVA (0,"java"),
    CPP (1,"C++"),
    GOLANG(2,"golang");

    private final Integer value;

    private final String desc;

    ProgramType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}

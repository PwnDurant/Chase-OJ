package com.zqq.common.core.enums;


import lombok.Getter;

@Getter
public enum QuestionResType {

    ERROR(0),
    PASS(1),
    UN_SUBMIT(2),
    IN_JUDGE(3);

    private final Integer value;

    QuestionResType(Integer value) {
        this.value = value;
    }
}

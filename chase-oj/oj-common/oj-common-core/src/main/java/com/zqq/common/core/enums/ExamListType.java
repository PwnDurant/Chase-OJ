package com.zqq.common.core.enums;

import lombok.Getter;

/**
 * 竞赛状态信息
 */
@Getter
public enum ExamListType {

//    未完赛
    EXAM_UN_FINISH_LIST(0),

//    历史竞赛
    EXAM_HISTORY_LIST(1),

//    用户竞赛
    USER_EXAM_LIST(2);

    private final Integer value;

    ExamListType(Integer value) {
        this.value = value;
    }
}

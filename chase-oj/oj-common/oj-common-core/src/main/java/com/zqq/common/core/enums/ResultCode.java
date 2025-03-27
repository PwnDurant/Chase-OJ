package com.zqq.common.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultCode {

    SUCCESS                         (1000,"操作成功"),

    ERROR                           (2000,"服务器繁忙，请稍后重试"),

    FAILED                          (3000,"操作失败"),
    FAILED_UNAUTHORIZED             (30001,"未授权"),
    FAILED_PARAMS_VALIDATE          (30002,"参数校验失败"),
    FAILED_NOT_EXISTS               (30003,"资源不存在"),
    FAILED_ALREADY_EXISTS           (30004,"资源已存在"),


    FAILED_USER_EXISTS              (31001,"用户已存在"),
    FAILED_USER_NOT_EXISTS          (31002,"用户不存在"),
    FAILED_LOGIN                    (31003,"用户名或密码错误"),
    FAILED_USER_BANNED              (31004,"您已被列入黑名单，请联系管理员"),

    EXAM_START_TIME_BEFORE_CURRENT_TIME(3201,"竞赛开始时间不能早于当前时间"),
    EXAM_START_TIME_AFTER_END_TIME(3202,"竞赛开始时间不能晚于竞赛结束时间"),
    EXAM_NOT_EXISTS                 (3203,"竞赛不存在"),
    EXAM_QUESTION_NOT_EXISTS        (3204,"为竞赛新增的题目不存在"),
    EXAM_QUESTION_EXISTS            (3205,"题目已经存在"),

    EXAM_STARTED                    (3206,"竞赛已经开始，不可以进行编辑"),
    DONT_EXISTS                     (3207,"此题目在竞赛中不存在"),

    EXAM_NOT_HAS_QUESTION           (3208,"竞赛不包含题目");


    private int code;

    private String msg;

}

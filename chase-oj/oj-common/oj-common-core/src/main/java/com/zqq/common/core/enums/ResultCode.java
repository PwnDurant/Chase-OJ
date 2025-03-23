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
    FAILED_USER_BANNED              (31004,"您已被列入黑名单，请联系管理员");

    private int code;

    private String msg;

}

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
    FAILED_USER_PHONE               (31005,"您输入的手机号码有误"),
    FAILED_FREQUENT                 (31006,"操作频繁，请稍后重试"),
    FAILED_TIME_LIMIT               (31007,"当天请求次数已经达到上限"),
    FAILED_SEND_CODE                (31008,"发送验证码失败"),
    FAILED_INVALID_CODE             (31009,"验证码无效"),
    FAILED_ERROR_CODE                (31010,"验证码错误"),

    EXAM_START_TIME_BEFORE_CURRENT_TIME(3201,"竞赛开始时间不能早于当前时间"),
    EXAM_START_TIME_AFTER_END_TIME(3202,"竞赛开始时间不能晚于竞赛结束时间"),
    EXAM_NOT_EXISTS                 (3203,"竞赛不存在"),
    EXAM_QUESTION_NOT_EXISTS        (3204,"为竞赛新增的题目不存在"),
    EXAM_QUESTION_EXISTS            (3205,"题目已经存在"),

    EXAM_STARTED                    (3206,"竞赛已经开始，不可以进行编辑"),
    DONT_EXISTS                     (3207,"此题目在竞赛中不存在"),

    EXAM_NOT_HAS_QUESTION           (3208,"竞赛不包含题目"),
    EXAM_IS_FINISH                  (3209,"竞赛已经结束，不能发布"),
    EXAM_IS_PUBLISH                 (3210,"竞赛发布，不能进行编辑"),

    USER_EXAM_HAS_ENTER             (3301,"用户已经报名，无需再次报名"),

    FAILED_FILE_UPLOAD                  (3401, "文件上传失败"),

    FAILED_FILE_UPLOAD_TIME_LIMIT       (3402, "当天上传图片数量超过上限"),

    FAILED_FIRST_QUESTION               (3501,"当前题目已经是第一题了"),
    FAILED_LAST_QUESTION               (3502,"当前题目已经是最后一题了"),

    FAILED_NOT_SUPPORT_PROGRAM          (3601,"当前不支持此语言"),

    FAILED_RABBIT_PRODUCE               (3701, "mq生产消息异常");





    private final int code;

    private final String msg;

}

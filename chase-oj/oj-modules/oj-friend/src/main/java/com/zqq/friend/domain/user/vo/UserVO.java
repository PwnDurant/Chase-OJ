package com.zqq.friend.domain.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 */
@Getter
@Setter
public class UserVO {

//    用户Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

//    用户昵称
    private String nickName;

//    用户头像
    private String headImage;

//    用户性别
    private Integer sex;

//    用户电话
    private String phone;

//    用户验证码
    private String code;

//    用户邮箱
    private String email;

//    用户微信
    private String wechat;

//    用户学校
    private String schoolName;

//    用户专业
    private String majorName;

//    用户自我介绍
    private String introduce;

//    用户状态 正常/拉黑
    private Integer status;
}

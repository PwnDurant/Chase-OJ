package com.zqq.common.core.domain;

import lombok.Data;

@Data
public class LoginUser {

    private Integer identity; // 1表示普通用户，2表示管理员用户

    private String nickName; //用户昵称

    private String headImage;

}

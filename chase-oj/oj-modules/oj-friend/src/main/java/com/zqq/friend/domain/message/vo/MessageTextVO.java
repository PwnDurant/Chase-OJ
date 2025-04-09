package com.zqq.friend.domain.message.vo;

import lombok.Getter;
import lombok.Setter;

//返回给前端，信息 信息id，信息标题，信息内容
@Getter
@Setter
public class MessageTextVO {

    private Long textId;

    private String messageTitle;

    private String messageContent;
}

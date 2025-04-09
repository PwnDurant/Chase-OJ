package com.zqq.job.domain.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zqq.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息内容
 */
@TableName("tb_message_text")
@Getter
@Setter
public class MessageText extends BaseEntity {

    @TableId(value = "TEXT_ID",type = IdType.ASSIGN_ID)
    private Long textId;

    private String messageTitle;

    private String messageContent;

}

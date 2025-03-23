package com.zqq.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    @TableField(fill= FieldFill.INSERT)
    private Long createBy;

    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill=FieldFill.UPDATE)
    private Long updateBy;

    @TableField(fill=FieldFill.UPDATE)
    private LocalDateTime updateTime;

}

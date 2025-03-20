package com.zqq.common.core.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    private Long createBy;

    private LocalDateTime dateTime;

    private Long updateBy;

    private LocalDateTime updateTime;

}

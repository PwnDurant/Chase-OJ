package com.zqq.system.domain.question.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionVO {
    private Long questionId;
    private String title;
    private Integer difficulty;
    private String createName;
    private LocalDateTime createTime;
}

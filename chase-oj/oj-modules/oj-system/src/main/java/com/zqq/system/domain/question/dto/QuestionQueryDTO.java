package com.zqq.system.domain.question.dto;

import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {

    private Integer difficulty;

    private String title;

}

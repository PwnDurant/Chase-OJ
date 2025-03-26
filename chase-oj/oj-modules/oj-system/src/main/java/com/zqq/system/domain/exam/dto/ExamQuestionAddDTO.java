package com.zqq.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;


@Getter
@Setter
public class ExamQuestionAddDTO {


    private Long examId;
//    可以保持顺序是正确的

    private LinkedHashSet<Long> questionIdSet;

}

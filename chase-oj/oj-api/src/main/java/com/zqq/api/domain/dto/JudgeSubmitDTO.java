package com.zqq.api.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JudgeSubmitDTO {

//    用户Id
    private Long userId;

//    竞赛Id
    private Long examId;

//    语种
    private Integer programType;

//    题目id
    private Long questionId;

//    难度
    private Integer difficulty;

//    时间限制
    private Long timeLimit;

//    空间限制
    private Long spaceLimit;

//    用户代码
    private String userCode;

//    输入的值
    private List<String> inputList;

//    输出的值
    private List<String> outputList;

}

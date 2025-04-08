package com.zqq.api.domain.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zqq.api.domain.UserExeResult;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserQuestionResultVO {

//    是否通过标识
    private Integer pass; // 0    未通过 1   通过

//    异常信息
    private String exeMessage;

//    返回信息
    private List<UserExeResult> userExeResultList;

    @JsonIgnore
    private Integer score;

}

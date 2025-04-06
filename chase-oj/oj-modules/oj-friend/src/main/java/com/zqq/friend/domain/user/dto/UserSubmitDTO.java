package com.zqq.friend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSubmitDTO {

    private Long examId; //可选

    private Long questionId;

    private Integer programType; // (1: java 2:cpp 3:golang)

    private String userCode;

}


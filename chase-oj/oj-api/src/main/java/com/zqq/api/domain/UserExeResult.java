package com.zqq.api.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserExeResult {

//    输入
    private String input;

//    期望输出
    private String exeOutput;

//    实际输出
    private String output;

}

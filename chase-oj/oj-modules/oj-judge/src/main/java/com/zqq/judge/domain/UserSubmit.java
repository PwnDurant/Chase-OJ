package com.zqq.judge.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zqq.common.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户提交数据表
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user_submit")
public class UserSubmit extends BaseEntity {

    @TableId(value = "SUBMIT_ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long submitId;

    private Long userId; //唯一确定用户     2001/2002

    private Long questionId;  //唯一确定题目  100      100     102

    private Long examId;   //唯一确定竞赛      1       2          3   4    null

    private Integer programType; //语种

    private String userCode; //用户代码

    private Integer pass;  //是否通过

    private Integer score;  //得分

    private String exeMessage;  //执行信息

    private String caseJudgeRes;  //案列测评结果
}

package com.zqq.friend.domain.exam.dto;

import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

    private Integer type; // 0:未完赛 1:历史竞赛 2:用户报名竞赛

}

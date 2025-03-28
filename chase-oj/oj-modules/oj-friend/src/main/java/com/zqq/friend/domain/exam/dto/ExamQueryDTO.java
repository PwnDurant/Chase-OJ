package com.zqq.friend.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

    private Integer type; // 0:未完赛 1:历史竞赛

}

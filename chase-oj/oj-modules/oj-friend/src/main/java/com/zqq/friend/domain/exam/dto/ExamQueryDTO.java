package com.zqq.friend.domain.exam.dto;

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


//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//    private LocalDateTime startTime;
//
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//    private LocalDateTime endTime;

    private Integer type; // 0:未完赛 1:历史竞赛 2:用户报名竞赛

}

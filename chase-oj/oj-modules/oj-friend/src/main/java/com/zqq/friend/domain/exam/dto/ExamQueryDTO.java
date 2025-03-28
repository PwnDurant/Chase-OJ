package com.zqq.friend.domain.exam.dto;

import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}

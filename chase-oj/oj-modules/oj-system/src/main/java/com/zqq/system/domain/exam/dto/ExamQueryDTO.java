package com.zqq.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zqq.common.core.domain.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

}

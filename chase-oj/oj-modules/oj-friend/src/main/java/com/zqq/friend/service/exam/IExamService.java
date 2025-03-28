package com.zqq.friend.service.exam;

import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

}

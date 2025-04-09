package com.zqq.friend.service.exam;

import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.dto.ExamRankDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface IExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);

    String getFirstQuestion(Long examId);

    String preQuestion(Long questionId, Long examId);

    String nextQuestion(Long questionId, Long examId);

    TableDataInfo rankList(ExamRankDTO examRankDTO);
}

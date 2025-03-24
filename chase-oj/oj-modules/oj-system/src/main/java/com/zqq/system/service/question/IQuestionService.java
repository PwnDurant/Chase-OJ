package com.zqq.system.service.question;

import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.question.dto.QuestionQueryDTO;
import com.zqq.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {

    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

}

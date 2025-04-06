package com.zqq.friend.service.question;

import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;
import com.zqq.friend.domain.question.vo.QuestionDetailVO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}

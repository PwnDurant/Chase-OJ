package com.zqq.friend.service.question;

import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);
}

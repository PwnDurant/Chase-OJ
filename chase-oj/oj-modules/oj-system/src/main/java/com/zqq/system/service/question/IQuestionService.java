package com.zqq.system.service.question;

import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.question.dto.QuestionAddDTO;
import com.zqq.system.domain.question.dto.QuestionEditDTO;
import com.zqq.system.domain.question.dto.QuestionQueryDTO;
import com.zqq.system.domain.question.vo.QuestionDetailVO;
import com.zqq.system.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {

    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(Long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int delete(Long questionId);
}

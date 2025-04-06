package com.zqq.judge.service;

import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.api.domain.vo.UserQuestionResultVO;

public interface IJudgeService {
    UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}

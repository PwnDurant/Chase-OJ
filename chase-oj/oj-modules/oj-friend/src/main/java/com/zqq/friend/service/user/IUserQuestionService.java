package com.zqq.friend.service.user;


import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.domain.R;
import com.zqq.friend.domain.user.dto.UserSubmitDTO;


public interface IUserQuestionService {


    R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);

    boolean rabbitSubmit(UserSubmitDTO userSubmitDTO);

    UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime);
}

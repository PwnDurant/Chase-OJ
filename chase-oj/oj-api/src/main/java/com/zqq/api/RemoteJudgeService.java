package com.zqq.api;


import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "RemoteJudgeService",value = Constants.JUDGE_SERVICE)
public interface RemoteJudgeService {

    @PostMapping("/judge/doJudgeJavaCode")
    R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);

}

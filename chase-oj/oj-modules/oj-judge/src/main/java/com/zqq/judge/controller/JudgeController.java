package com.zqq.judge.controller;

import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.judge.service.IJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/judge")
public class JudgeController extends BaseController {

    @Autowired
    private IJudgeService judgeService;

    @PostMapping("/doJudgeJavaCode")
    public R<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO){
        return R.ok(judgeService.doJudgeJavaCode(judgeSubmitDTO));
    }

}

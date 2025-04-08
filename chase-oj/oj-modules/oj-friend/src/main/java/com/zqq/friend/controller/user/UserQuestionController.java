package com.zqq.friend.controller.user;

import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.friend.domain.user.dto.UserSubmitDTO;
import com.zqq.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController {

    @Autowired
    private IUserQuestionService userQuestionService;


//    用户代码提交。请求方法。地址  参数  响应数据结构

    /**
     * 提交代码并返回结果
     * @param submitDTO 提交的内容：UserSubmitDTO
     * @return 返回结果和实际输出和预期输出
     */
    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO){
        return userQuestionService.submit(submitDTO);
    }

    /**
     * 使用rabbitmq进行提交代码
     * @param userSubmitDTO
     * @return
     */
    @PostMapping("/rabbit/submit")
    public R<Void> rabbitSubmit(@RequestBody UserSubmitDTO userSubmitDTO){
        return toR(userQuestionService.rabbitSubmit(userSubmitDTO));
    }

    @GetMapping("/exe/result")
    public R<UserQuestionResultVO> exeResult(Long examId,Long questionId,String currentTime){
        return R.ok(userQuestionService.exeResult(examId,questionId,currentTime));
    }

}

package com.zqq.friend.controller.question;

import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;
import com.zqq.friend.domain.question.vo.QuestionDetailVO;
import com.zqq.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;

    @GetMapping("/semiLogin/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
        return questionService.list(questionQueryDTO);
    }

    /**
     * 点击题目页面展示题目详情
     * @param questionId 题目Id
     * @return 题目详情
     */
    @GetMapping("/detail")
    public R<QuestionDetailVO> detail(Long questionId){
        return R.ok(questionService.detail(questionId));
    }

    /**
     * 题目的顺序列表 当前题目：questionId
     * redis list 数据类型: key: q:l   value: questionId
     * @param questionId 题目Id
     * @return 前一题
     */
    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long questionId){
        return R.ok(questionService.preQuestion(questionId));
    }

    /**
     * 题目的顺序列表 当前题目：questionId
     * redis list 数据类型: key: q:l   value: questionId
     * @param questionId 题目Id
     * @return 后一题
     */
    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long questionId){
        return R.ok(questionService. nextQuestion(questionId));
    }

}

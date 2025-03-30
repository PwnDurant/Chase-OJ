package com.zqq.friend.controller.question;

import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;
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

}

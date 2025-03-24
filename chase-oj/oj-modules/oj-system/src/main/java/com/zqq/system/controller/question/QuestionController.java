package com.zqq.system.controller.question;


import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.question.dto.QuestionQueryDTO;
import com.zqq.system.domain.question.vo.QuestionVO;
import com.zqq.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/question")
@Tag(name = "项目管理接口")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;


    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
         return getTableDataInfo(questionService.list(questionQueryDTO));
    }
}

package com.zqq.system.service.question.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.question.dto.QuestionQueryDTO;
import com.zqq.system.domain.question.vo.QuestionVO;
import com.zqq.system.mapper.question.QuestionMapper;
import com.zqq.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {

//        这段代码主要作用是开启分页，会拦截下来紧跟的SQL查询，并在查询时自动拼接分页的LIMIT语句，所以这里不需要有返回值
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());

        return  questionMapper.selectQuestionList(questionQueryDTO);

    }
}

package com.zqq.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.system.domain.question.Question;
import com.zqq.system.domain.question.dto.QuestionAddDTO;
import com.zqq.system.domain.question.dto.QuestionEditDTO;
import com.zqq.system.domain.question.dto.QuestionQueryDTO;
import com.zqq.system.domain.question.vo.QuestionDetailVO;
import com.zqq.system.domain.question.vo.QuestionVO;
import com.zqq.system.mapper.question.QuestionMapper;
import com.zqq.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        String excludeIdStr= questionQueryDTO.getExcludeIdStr();
        if(StrUtil.isNotEmpty(excludeIdStr)){
            String[] excludeIdArr=excludeIdStr.split(Constants.SPLIT_SEM);
            Set<Long> excludeIdSet= Arrays.stream(excludeIdArr)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            questionQueryDTO.setExcludeIdSet(excludeIdSet);
        }
//        这段代码主要作用是开启分页，会拦截下来紧跟的SQL查询，并在查询时自动拼接分页的LIMIT语句，所以这里不需要有返回值
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());
        return  questionMapper.selectQuestionList(questionQueryDTO);
    }

    @Override
    public int add(QuestionAddDTO questionAddDTO) {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getTitle, questionAddDTO.getTitle()));
        if(CollectionUtil.isNotEmpty(questions)){
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        Question question=new Question();
        BeanUtil.copyProperties(questionAddDTO,question);
        return  questionMapper.insert(question);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if(question==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        QuestionDetailVO questionDetailVO=new QuestionDetailVO();
        BeanUtil.copyProperties(question,questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question oldQuestion = questionMapper.selectById(questionEditDTO.getQuestionId());
        if(oldQuestion==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
//        浅拷贝
        BeanUtil.copyProperties(questionEditDTO,oldQuestion);
        return questionMapper.updateById(oldQuestion);
    }

    @Override
    public int delete(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if(question==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        return questionMapper.deleteById(questionId);
    }
}

package com.zqq.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.Question;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;
import com.zqq.friend.domain.question.es.QuestionES;
import com.zqq.friend.domain.question.vo.QuestionVO;
import com.zqq.friend.elasticsearch.QuestionRepository;
import com.zqq.friend.mapper.question.QuestionMapper;
import com.zqq.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        long count=questionRepository.count();
//       判断es中有没有数据
        if(count<=0){
            refreshQuestion();
        }
        Sort createTime = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageRequest = PageRequest.of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), createTime);
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyWord = questionQueryDTO.getKeyWord();
        Page<QuestionES> questionESPage;
        if(difficulty==null&& StrUtil.isEmpty(keyWord)){
            questionESPage = questionRepository.findAll(pageRequest);
        }else if(StrUtil.isEmpty(keyWord)){
            questionESPage=questionRepository.findQuestionByDifficulty(difficulty,pageRequest);
        }else if(difficulty==null){
            questionESPage=questionRepository.findByTitleOrContent(keyWord,keyWord,pageRequest);
        }else{
            questionESPage=questionRepository.findByTitleOrContentAndDifficulty(keyWord,keyWord,difficulty,pageRequest);
        }
        long total = questionESPage.getTotalElements();
        if(total<=0){
            return TableDataInfo.empty();
        }
        List<QuestionES> questionESList = questionESPage.getContent();
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionESList, QuestionVO.class);
        return TableDataInfo.success(questionVOList,total);
    }

    private void refreshQuestion() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if(CollectionUtil.isEmpty(questionList)) {
            return;
        }
        List<QuestionES> questionESList = BeanUtil.copyToList(questionList, QuestionES.class);
        questionRepository.saveAll(questionESList);
    }


}

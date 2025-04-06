package com.zqq.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.question.Question;
import com.zqq.friend.domain.question.dto.QuestionQueryDTO;
import com.zqq.friend.domain.question.es.QuestionES;
import com.zqq.friend.domain.question.vo.QuestionDetailVO;
import com.zqq.friend.domain.question.vo.QuestionVO;
import com.zqq.friend.elasticsearch.QuestionRepository;
import com.zqq.friend.manage.QuestionCacheManage;
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
    @Autowired
    private QuestionCacheManage questionCacheManage;

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

    @Override
    public QuestionDetailVO detail(Long questionId) {
//        根据questionId从elasticsearch中查处对应的题目
        QuestionES questionES=questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO=new QuestionDetailVO();
        if(questionES!=null){
            BeanUtil.copyProperties(questionES,questionDetailVO);
            return questionDetailVO;
        }
//        走到这说明缓存里面没有对应记录，再从数据库中查找，如有就刷新到缓存里，没有就直接返回
        Question question=questionMapper.selectById(questionId);
        if(question==null){
            return null;
        }
        refreshQuestion();
        BeanUtil.copyProperties(question,questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public String preQuestion(Long questionId) {
        Long listSize=questionCacheManage.getListSize();
        if(listSize==null||listSize<=0){
            questionCacheManage.refreshCache();
        }
        return questionCacheManage.preQuestion(questionId).toString();
    }

    @Override
    public String nextQuestion(Long questionId) {
        Long listSize=questionCacheManage.getListSize();
        if(listSize==null||listSize<=0){
            questionCacheManage.refreshCache();
        }
        return questionCacheManage.nextQuestion(questionId).toString();
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

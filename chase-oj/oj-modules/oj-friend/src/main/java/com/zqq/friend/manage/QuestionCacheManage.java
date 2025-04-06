package com.zqq.friend.manage;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.domain.question.Question;
import com.zqq.friend.mapper.question.QuestionMapper;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManage {

    @Autowired
    private RedisService redisService;

    @Autowired
    private QuestionMapper questionMapper;



    public Long getListSize(){
        return redisService.getListSize(CacheConstants.QUESTION_LIST);
    }

    public void refreshCache(){
        List<Question> questionList=questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId).orderByDesc(Question::getCreateTime));
        if(CollectionUtil.isEmpty(questionList)){
            return ;
        }
        List<Long> questionIdList=questionList.stream().map(Question::getQuestionId).toList();
        redisService.rightPushAll(CacheConstants.QUESTION_LIST,questionIdList);
    }

    public Long preQuestion(Long questionId){
//        Long index=redisService.indexOf(CacheConstants.QUESTION_LIST,questionId);
        Long index=redisService.indexOfForList(CacheConstants.QUESTION_LIST,questionId);
        if(index==0){
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        return redisService.indexForList(CacheConstants.QUESTION_LIST,index-1,Long.class);
    }

    public Long nextQuestion(Long questionId){
        Long index=redisService.indexOfForList(CacheConstants.QUESTION_LIST,questionId);
        long lastIndex=getListSize()-1;
        if(index==lastIndex){
            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
        }
        return redisService.indexForList(CacheConstants.QUESTION_LIST,index+1,Long.class);
    }

}

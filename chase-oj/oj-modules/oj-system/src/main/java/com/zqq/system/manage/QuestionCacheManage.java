package com.zqq.system.manage;


import com.zqq.common.core.constants.CacheConstants;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionCacheManage {

    @Autowired
    private RedisService redisService;

    public void addCache(Long questionId){
        redisService.leftPushForList(CacheConstants.QUESTION_LIST,questionId);
    }

    public void deleteCache(Long questionId){
        redisService.removeForList(CacheConstants.QUESTION_LIST,questionId);
    }

}

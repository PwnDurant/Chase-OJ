package com.zqq.system.manage;


import com.zqq.common.core.constants.CacheConstants;
import com.zqq.redis.service.RedisService;
import com.zqq.system.domain.exam.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 竞赛缓存功能
 */
@Component
public class ExamCacheManage {

    @Autowired
    private RedisService redisService;

    /**
     * 发布竞赛的时候，将竞赛的信息放入两个缓存中（一个里面存放竞赛Id，一个里面存放竞赛详情信息
     * @param exam
     */
    public void addCache(Exam exam){
        redisService.leftPushForList(getExamListKey(),exam.getExamId());
        redisService.setCacheObject(getDetailKey(exam.getExamId()),exam);
    }

    /**
     * 同上
     * @param examId
     */
    public void deleteCache(Long examId){
        redisService.removeForList(getExamListKey(),examId);
        redisService.deleteObject(getDetailKey(examId));
    }

    private String getExamListKey() {
        return CacheConstants.EXAM_UNFINISHED_LIST;
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL+examId;
    }



}

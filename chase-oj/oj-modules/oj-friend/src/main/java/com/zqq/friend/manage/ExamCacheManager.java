package com.zqq.friend.manage;

import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.enums.ExamListType;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamCacheManager {

    @Autowired
    private RedisService redisService;

    /**
     * 获取竞赛列表的长度
     * @param examListType
     * @param userId
     * @return
     */
    public Long getListSize(Integer examListType, Long userId) {
        String examListKey = getExamListKey(examListType, userId);
        return redisService.getListSize(examListKey);
    }

    /**
     * 获取竞赛列表key
     * @param examListType
     * @param userId
     * @return
     */
    private String getExamListKey(Integer examListType,Long userId){
//        如果是未完赛的话就返回key: e:t:l:
//        如果是历史竞赛返回key: e:h:l
//        都不是返回用户竞赛key: u:e:l:userId
        if(ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)){
            return CacheConstants.EXAM_UNFINISHED_LIST;
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_HISTORY_LIST;
        }else {
            return CacheConstants.USER_EXAM_LIST+userId;
        }
    }

    private String getDetailKey(Long examId){
        return CacheConstants.EXAM_DETAIL+examId;
    }

    private String getUserExamListKey(Long userId){
        return CacheConstants.USER_EXAM_LIST+userId;
    }

}

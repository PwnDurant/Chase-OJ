package com.zqq.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.friend.domain.exam.dto.ExamRankDTO;
import com.zqq.friend.manager.ExamCacheManager;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.service.exam.IExamService;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamCacheManager examCacheManager;
    @Autowired
    private RedisService redisService;


    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long listSize = examCacheManager.getListSize(examQueryDTO.getType(),null);
        List<ExamVO> examVOList = null;
        if(listSize==null||listSize==0){
            examVOList=list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
            listSize=new PageInfo<>(examVOList).getTotal();
        }else{
            examCacheManager.getExamVOList(examQueryDTO,null);
            listSize = examCacheManager.getListSize(examQueryDTO.getType(),null);
        }

        if(CollectionUtil.isEmpty(examVOList)){
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList,listSize);
    }

    @Override
    public String getFirstQuestion(Long examId) {
        checkAndRefresh(examId);
        return examCacheManager.getFirstQuestion(examId).toString();
    }

    @Override
    public String preQuestion(Long questionId, Long examId) {
        checkAndRefresh(examId);
        return examCacheManager.preQuestion(examId,questionId).toString();
    }

    @Override
    public String nextQuestion(Long questionId, Long examId) {
        checkAndRefresh(examId);
        return examCacheManager.nextQuestion(examId,questionId).toString();
    }

    @Override
    public TableDataInfo rankList(ExamRankDTO examRankDTO) {
        return null;
    }

    //    查找并刷新
    private void checkAndRefresh(Long examId) {
//拿到竞赛列表长度
        Long listSize=examCacheManager.getExamQuestionListSize(examId);
//        判断是否需要进行刷新
        if(listSize==null||listSize<=0){
            examCacheManager.refreshExamQuestionCache(examId);
        }

    }

    //    设置报名状态
    private void assembleExamVOList(List<ExamVO> examVOList){
        Long userId= ThreadLocalIUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamIdList=examCacheManager.getAllUserExamList(userId);

        for(ExamVO examVO:examVOList){
            if(userExamIdList.contains(examVO.getExamId())){
                examVO.setEnter(true);
            }
        }
    }
}

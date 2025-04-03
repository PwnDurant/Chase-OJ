package com.zqq.friend.manage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ExamListType;
import com.zqq.friend.domain.exam.Exam;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.mapper.user.UserExamMapper;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExamCacheManager {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserExamMapper userExamMapper;

    /**
     * 获取竞赛列表的长度
     * @param examListType 要查询的竞赛种类
     * @param userId 用户Id
     * @return 从redis中查询出的竞赛长度
     */
    public Long getListSize(Integer examListType, Long userId) {
        String examListKey = getExamListKey(examListType, userId);
        return redisService.getListSize(examListKey);
    }

    /**
     * 返回竞赛列表数据
     * @param examQueryDTO 标题，开始/结束时间
     * @return 竞赛列表数据
     */
    public List<ExamVO> getExamVOList(ExamQueryDTO examQueryDTO,Long userId){
        int start=(examQueryDTO.getPageNum()-1)*examQueryDTO.getPageSize();
        int end=start+examQueryDTO.getPageSize()-1;     //下标需要-1
//        根据开始和结束下标和key值查询redis中对应的竞赛Id
        String examListKey = getExamListKey(examQueryDTO.getType(),userId);
        List<Long> examIdList = redisService.getCacheListByRange(examListKey, start, end, Long.class);
//        根据Id去查询对应的数据
        List<ExamVO> examVOList=assembleExamVOList(examIdList);
        if(CollectionUtil.isEmpty(examVOList)){
//            说明redis中数据可能有问题，从数据库中查询数据，并重新刷新缓存
//
            examVOList=getExamListByDB(examQueryDTO,userId); //从数据库中获取数据
            refreshCache(examQueryDTO.getType(),userId);
        }
        return examVOList;
    }

    /**
     * 刷新缓存
     * @param examListType 竞赛类别
     * @param userId 用户Id
     */
    public void refreshCache(Integer examListType, Long userId) {

        List<Exam> examList=new ArrayList<>();
        if(ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)){
//            查询未完赛的竞赛列表
            examList=examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId,Exam::getTitle,Exam::getStartTime,Exam::getEndTime)
                    .gt(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByDesc(Exam::getCreateTime));
        }else if(ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)){
//            查询历史竞赛
            examList=examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId,Exam::getTitle,Exam::getStartTime,Exam::getEndTime)
                    .le(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByDesc(Exam::getCreateTime));
        }else if (ExamListType.USER_EXAM_LIST.getValue().equals(examListType)){
            List<ExamVO> examVOList=userExamMapper.selectUserExamList(userId);
            examList= BeanUtil.copyToList(examVOList, Exam.class);
        }
        if(CollectionUtil.isEmpty(examList)){
            return ;
        }

        Map<String,Exam> examMap=new HashMap<>();
        List<Long> examIdList=new ArrayList<>();
        for(Exam exam:examList){
            examMap.put(getDetailKey(exam.getExamId()),exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap); //刷新详情缓存
        redisService.deleteObject(getExamListKey(examListType,userId));
        redisService.rightPushAll(getExamListKey(examListType,userId),examIdList);
    }

    /**
     * 从数据库中查询数据
     * @param examQueryDTO  竞赛信息
     * @param userId 用户Id
     * @return 从数据库中查到的数据
     */
    private List<ExamVO> getExamListByDB(ExamQueryDTO examQueryDTO, Long userId) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        if (ExamListType.USER_EXAM_LIST.getValue().equals(examQueryDTO.getType())) {
            //查询我的竞赛列表
            return userExamMapper.selectUserExamList(userId);
        } else {
            //查询C端的竞赛列表
            return examMapper.selectExamList(examQueryDTO);
        }
    }

    /**
     * 根据得到的竞赛Id列表，从缓存中获取对应的竞赛列表
     * @param examIdList 竞赛Id列表
     * @return 返回竞赛列表信息
     */
    //    redis缓存结构中，u:e:l:examId   e:d:detail
    private List<ExamVO> assembleExamVOList(List<Long> examIdList) {
        if(CollectionUtil.isEmpty(examIdList)){
//            说明redis中没有对应的竞赛缓存
            return null;
        }
        //拼接redis当中key的方法 并且将拼接好的key存储到一个list中
        List<String> detailKeyList = new ArrayList<>();
        for (Long examId : examIdList) {
            detailKeyList.add(getDetailKey(examId));
        }
//        批量从缓存中获取数据
        List<ExamVO> examVOList = redisService.multiGet(detailKeyList, ExamVO.class);
        CollUtil.removeNull(examVOList);
        if (CollectionUtil.isEmpty(examVOList) || examVOList.size() != examIdList.size()) {
            //说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
            return null;
        }
        return examVOList;
    }

    /**
     * 获取竞赛列表key
     * @param examListType 要查询的竞赛种类
     * @param userId 用户Id
     * @return 在redis中的key结构
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

    /**
     * 获取竞赛详情的key
     * @param examId 竞赛Id
     * @return 返回e:d:examId
     */
    private String getDetailKey(Long examId){
        return CacheConstants.EXAM_DETAIL+examId;
    }

    /**
     * 获取用户竞赛列表的Key
     * @param userId 用户Id
     * @return 返回u:e:l:userId
     */
    private String getUserExamListKey(Long userId){
        return CacheConstants.USER_EXAM_LIST+userId;
    }

    /**
     * 为报名过的用户在缓存中添加一条记录
     * @param userId 用户Id
     * @param examId 竞赛Id
     */
    public void addUserExamCache(Long userId, Long examId) {
        String userExamListKey = getUserExamListKey(userId);
        redisService.leftPushForList(userExamListKey,examId);
    }
}

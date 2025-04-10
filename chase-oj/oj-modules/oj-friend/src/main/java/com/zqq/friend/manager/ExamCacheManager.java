package com.zqq.friend.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ExamListType;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.domain.exam.Exam;
import com.zqq.friend.domain.exam.ExamQuestion;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.dto.ExamRankDTO;
import com.zqq.friend.domain.exam.vo.ExamRankVO;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.domain.user.UserExam;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.mapper.exam.ExamQuestionMapper;
import com.zqq.friend.mapper.user.UserExamMapper;
import com.zqq.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ExamCacheManager {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserExamMapper userExamMapper;
    @Autowired
    private ExamQuestionMapper examQuestionMapper;

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
//        List<ExamVO> examVOList=assembleExamVOList(examIdList);

//        先暂时在数据库中查询
        List<ExamVO> examVOList=examMapper.selectExamList(examQueryDTO);

        if(CollectionUtil.isEmpty(examVOList)){
//            说明redis中数据可能有问题，从数据库中查询数据，并重新刷新缓存
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
     * 刷新考试排名缓存
     * @param examId 竞赛列表
     */
    public void refreshExamRankCache(Long examId){
        List<ExamRankVO> examRankVOList=userExamMapper.selectExamRankList(examId);
        if(CollectionUtil.isEmpty(examRankVOList)){
            return;
        }
        redisService.rightPushAll(getExamRankListKey(examId),examRankVOList);
    }

    private String getExamRankListKey(Long examId) {
        return CacheConstants.EXAM_RANK_LIST+examId;
    }

    /**
     * 获取考试排名列表
     * @param examRankDTO 竞赛排名信息
     * @return 返回考试排名列表
     */
    public List<ExamRankVO> getExamRankList(ExamRankDTO examRankDTO){
        int start=(examRankDTO.getPageNum()-1)*examRankDTO.getPageSize();
        int end=start+examRankDTO.getPageSize()-1;
        return redisService.getCacheListByRange(getExamRankListKey(examRankDTO.getExamId()),start,end, ExamRankVO.class);
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

    /**
     * 根据用户id，拿到用户报名参加过的竞赛ID列表
     * @param userId 用户Id
     * @return 返回用户报名过的竞赛Id列表
     */
    public List<Long> getAllUserExamList(Long userId) {

        String examListKey=CacheConstants.USER_EXAM_LIST+ userId;
//        先根据用户Id查处所报名的竞赛Id列表，在缓存中
        List<Long> userExamIdList=redisService.getCacheListByRange(examListKey,0,-1, Long.class);
        if(CollectionUtil.isNotEmpty(userExamIdList)){
            return userExamIdList;
        }
//        查处用户所报名的竞赛，在数据库中
        List<UserExam> userExamList=userExamMapper.selectList(new LambdaQueryWrapper<UserExam>()
                .eq(UserExam::getUserId,userId));
        if(CollectionUtil.isEmpty(userExamList)){
            return null;
        }
//        到这就说明缓存中数据和数据库中数据不一致，需要进行同步
        refreshCache(ExamListType.USER_EXAM_LIST.getValue(),userId);
        return userExamList.stream().map(UserExam::getExamId).toList();
    }

    public Long getFirstQuestion(Long examId) {
        return redisService.indexForList(getExamQuestionListKey(examId),0,Long.class);
    }

    private String getExamQuestionListKey(Long examId) {

        return CacheConstants.EXAM_QUESTION_LIST+examId;
    }

    public Long getExamQuestionListSize(Long examId) {
        String examQuestionListKey=getExamQuestionListKey(examId);
        return redisService.getListSize(examQuestionListKey);
    }

    public void refreshExamQuestionCache(Long examId) {
        List<ExamQuestion> examQuestionList=examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId,examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if(CollectionUtil.isEmpty(examQuestionList)){
            return;
        }
        List<Long> examQuestionIdList=examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
        redisService.rightPushAll(getExamQuestionListKey(examId),examQuestionIdList);
//        节省redis资源，放一天就行了
        long seconds= ChronoUnit.SECONDS.between(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        redisService.expire(getExamQuestionListKey(examId),seconds, TimeUnit.SECONDS);
    }

    public Long preQuestion(Long examId, Long questionId) {

        Long index=redisService.indexOfForList(getExamQuestionListKey(examId),questionId);
        if(index==0){
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        return redisService.indexForList(getExamQuestionListKey(examId),index-1,Long.class);

    }

    public Long nextQuestion(Long examId, Long questionId) {

        Long index=redisService.indexOfForList(getExamQuestionListKey(examId),questionId);
        long lastIndex=getExamQuestionListSize(examId)-1;
        if(index==lastIndex){
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        return redisService.indexForList(getExamQuestionListKey(examId),index+1,Long.class);

    }

    public Long getRankListSize(Long examId) {
        return redisService.getListSize(getExamRankListKey(examId));
    }
}

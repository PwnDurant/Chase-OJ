package com.zqq.job.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zqq.common.core.constants.CacheConstants;
import com.zqq.common.core.constants.Constants;
import com.zqq.job.domain.exam.Exam;
import com.zqq.job.domain.message.Message;
import com.zqq.job.domain.message.MessageText;
import com.zqq.job.domain.message.vo.MessageTextVO;
import com.zqq.job.domain.user.UserScore;
import com.zqq.job.mapper.exam.ExamMapper;
import com.zqq.job.mapper.message.MessageMapper;
import com.zqq.job.mapper.message.MessageTextMapper;
import com.zqq.job.mapper.user.UserExamMapper;
import com.zqq.job.mapper.user.UserSubmitMapper;
import com.zqq.job.service.IMessageService;
import com.zqq.job.service.IMessageTextService;
import com.zqq.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExamXxlJob {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IMessageTextService messageTextService;

    @Autowired
    private UserExamMapper userExamMapper;



    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler() {
        //  统计哪些竞赛应该存入未完赛的列表中  哪些竞赛应该存入历史竞赛列表中   统计出来了之后，再存入对应的缓存中
        log.info("*** examListOrganizeHandler ***");

//        	•	查询所有“未结束”的竞赛（endTime > 当前时间 且 status = 启用）；
//	        •	选出所需字段（id、标题、开始/结束时间）；
//	        •	存入缓存常量 EXAM_UNFINISHED_LIST 中。
        List<Exam> unFinishList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(unFinishList, CacheConstants.EXAM_UNFINISHED_LIST);

//        	•	和上面逻辑类似，只不过是“已结束”的比赛（endTime <= 当前时间）；
//	        •	存入缓存常量 EXAM_HISTORY_LIST 中
        List<Exam> historyList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByDesc(Exam::getCreateTime));
        refreshCache(historyList, CacheConstants.EXAM_HISTORY_LIST);

        log.info("*** examListOrganizeHandler 统计结束 ***");
    }

    @XxlJob("examResultHandler")
    public void examResultHandler() {
//        	•	now 表示当前时间。
//	        •	minusDateTime 表示 24 小时前的时间（即“昨天”）。
//	        •	这一对时间是为了查找在这 24 小时内刚刚结束的竞赛。
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusDateTime = now.minusDays(1);
//        	•	查找“昨天到现在之间结束的竞赛”（就是刚刚结束的）；
//	        •	只选出 examId 和 title 字段；
//	        •	前提是竞赛是“启用状态”的
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle)
                .eq(Exam::getStatus, Constants.TRUE)
                .ge(Exam::getEndTime, minusDateTime)
                .le(Exam::getEndTime, now));
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }
//        	•	提取 examList 中每个竞赛的 ID，放入一个 Set<Long> 中，用于后续查询
        Set<Long> examIdSet = examList.stream().map(Exam::getExamId).collect(Collectors.toSet());
//        	•	调用 userSubmitMapper 从数据库中查询指定竞赛 ID 集合的用户得分记录；
//	        •	返回的是一个 UserScore 列表（你可以理解为：每个人在每场比赛的得分信息）。
        List<UserScore> userScoreList = userSubmitMapper.selectUserScoreList(examIdSet);
//        	•	把所有用户的成绩按 examId 归类，形成一个 map
        Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));
        createMessage(examList, userScoreMap);
    }

    private void createMessage(List<Exam> examList, Map<Long, List<UserScore>> userScoreMap) {
//        	•	MessageText：每条消息的内容
//	        •	Message：每条消息的记录（谁发给谁）
        List<MessageText> messageTextList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();

//        	•	取出每场考试的成绩列表；
//	        •	计算总人数；
//	        •	examRank 初始为 1，用于设置排名
        for (Exam exam : examList) {
            Long examId = exam.getExamId();
            List<UserScore> userScoreList = userScoreMap.get(examId);
            int totalUser = userScoreList.size();
            int examRank = 1;

            for (UserScore userScore : userScoreList) {
                String msgTitle =  exam.getTitle() + "——排名情况";
                String msgContent = "您所参与的竞赛：" + exam.getTitle()
                        + "，本次参与竞赛一共" + totalUser + "人， 您排名第"  + examRank + "名！";
                userScore.setExamRank(examRank);
                MessageText messageText = new MessageText();
                messageText.setMessageTitle(msgTitle);
                messageText.setMessageContent(msgContent);
                messageText.setCreateBy(Constants.SYSTEM_USER_ID);
                messageTextList.add(messageText);
                Message message = new Message();
                message.setSendId(Constants.SYSTEM_USER_ID);
                message.setCreateBy(Constants.SYSTEM_USER_ID);
                message.setRecId(userScore.getUserId());
                messageList.add(message);
                examRank++;
            }
            userExamMapper.updateUserScoreAndRank(userScoreList);
            redisService.rightPushAll(getExamRankListKey(examId), userScoreList);
        }

        messageTextService.batchInsert(messageTextList);
        Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
        for (int i = 0; i < messageTextList.size(); i++) {
            MessageText messageText = messageTextList.get(i);
            MessageTextVO messageTextVO = new MessageTextVO();
            BeanUtil.copyProperties(messageText, messageTextVO);
            String msgDetailKey = getMsgDetailKey(messageText.getTextId());
            messageTextVOMap.put(msgDetailKey, messageTextVO);
            Message message = messageList.get(i);
            message.setTextId(messageText.getTextId());
        }
        messageService.batchInsert(messageList);
        //redis 操作
        Map<Long, List<Message>> userMsgMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
        Iterator<Map.Entry<Long, List<Message>>> iterator = userMsgMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<Message>> entry = iterator.next();
            Long recId = entry.getKey();
            String userMsgListKey = getUserMsgListKey(recId);
            List<Long> userMsgTextIdList = entry.getValue().stream().map(Message::getTextId).toList();
            redisService.rightPushAll(userMsgListKey, userMsgTextIdList);
        }
        redisService.multiSet(messageTextVOMap);
    }


    public void refreshCache(List<Exam> examList, String examListKey) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }

        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Exam exam : examList) {
            examMap.put(getDetailKey(exam.getExamId()), exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap);  //刷新详情缓存
        redisService.deleteObject(examListKey);
        redisService.rightPushAll(examListKey, examIdList);      //刷新列表缓存
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getUserMsgListKey(Long userId) {
        return CacheConstants.USER_MESSAGE_LIST + userId;
    }

    private String getMsgDetailKey(Long textId) {
        return CacheConstants.MESSAGE_DETAIL + textId;
    }

    private String getExamRankListKey(Long examId) {
        return CacheConstants.EXAM_RANK_LIST + examId;
    }
}

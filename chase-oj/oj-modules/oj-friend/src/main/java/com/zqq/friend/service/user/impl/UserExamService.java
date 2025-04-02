package com.zqq.friend.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.common.security.service.TokenService;
import com.zqq.friend.domain.exam.Exam;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.user.UserExam;
import com.zqq.friend.manage.ExamCacheManager;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.mapper.user.UserExamMapper;
import com.zqq.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserExamService implements IUserExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private ExamCacheManager examCacheManager;


    @Override
    public int enter(String token, Long examId) {
//        根据传入的竞赛Id从数据库中找出对应的竞赛并进行判断
        Exam exam = examMapper.selectById(examId);
        if(exam==null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        if(exam.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
//        从token中拿到用户的Id,并且根据用户Id和竞赛Id拿到用户竞赛信息
//        Long userId = tokenService.getUserId(token, secret);
//        从线程变量中获取
        Long userId = ThreadLocalIUtil.get(Constants.USER_ID, Long.class);
        UserExam userExam = userExamMapper.selectOne(new LambdaQueryWrapper<UserExam>()
                .eq(UserExam::getExamId, examId)
                .eq(UserExam::getUserId, userId));
//        进一步判断用户是否之前已经报过名
        if(userExam!=null){
            throw new ServiceException(ResultCode.USER_EXAM_HAS_ENTER);
        }
//        在缓存中加入信息 redis表结构 key: u:e:l:userId value:examId
        examCacheManager.addUserExamCache(userId,examId);
        userExam = new UserExam();
        userExam.setExamId(examId);
        userExam.setUserId(userId);
//        在redis插入之后再给数据库也插入一份
        return userExamMapper.insert(userExam);
    }

    @Override
    public void list(ExamQueryDTO examQueryDTO) {

    }
}

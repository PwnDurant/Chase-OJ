package com.zqq.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.friend.domain.user.UserSubmit;

import java.util.List;


public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

//    查找当前用户提交
    UserSubmit selectCurrentUserSubmit(Long userId, Long examId, Long questionId, String currentTime);

//    查找题目列表
    List<Long> selectHostQuestionList();
}

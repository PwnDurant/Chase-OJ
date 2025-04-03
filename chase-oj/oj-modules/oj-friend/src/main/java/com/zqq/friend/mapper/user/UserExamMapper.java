package com.zqq.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.domain.user.UserExam;

import java.util.List;


public interface UserExamMapper extends BaseMapper<UserExam> {

    /**
     * 根据UserId查询当前用户所报名的竞赛
     * @param userId 用户Id
     * @return 返回查询出的竞赛列表信息
     */
    List<ExamVO> selectUserExamList(Long userId);
}

package com.zqq.friend.service.user;

import com.zqq.friend.domain.exam.dto.ExamQueryDTO;

public interface IUserExamService {

    int enter(String token, Long examId);

    void list(ExamQueryDTO examQueryDTO);
}

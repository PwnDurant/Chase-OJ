package com.zqq.friend.controller.user;

import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.exam.dto.ExamDTO;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * C端用户竞赛相关功能
 */
@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {

    @Autowired
    private IUserExamService userExamService;

    /**
     * 用户报名接口
     * @param token 用户身份token令牌
     * @param examDTO 前端传给的竞赛信息 使用的是里面的竞赛Id
     * @return 插入数据库是否成功
     */
    @PostMapping("/enter")
    public R<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO){
        return toR(userExamService.enter(token, examDTO.getExamId()));
    }

    /**
     *
     * @param examQueryDTO
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return userExamService.list(examQueryDTO);
    }

}

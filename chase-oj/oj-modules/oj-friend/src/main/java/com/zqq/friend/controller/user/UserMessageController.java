package com.zqq.friend.controller.user;

import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.PageQueryDTO;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.service.user.IUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内信
 */
@RestController
@RequestMapping("/user/message")
public class UserMessageController extends BaseController {

    @Autowired
    private IUserMessageService userMessageService;

    @GetMapping("/list")
    public TableDataInfo list(PageQueryDTO dto){
        return userMessageService.list(dto);
    }


}

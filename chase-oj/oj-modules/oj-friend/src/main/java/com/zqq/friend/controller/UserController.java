package com.zqq.friend.controller;


import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.friend.domain.dto.UserDTO;
import com.zqq.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        userService.sendCode(userDTO);
    }
}

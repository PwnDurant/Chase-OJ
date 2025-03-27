package com.zqq.system.controller.user;

import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.user.dto.UserDTO;
import com.zqq.system.domain.user.dto.UserQueryDTO;
import com.zqq.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    /**
     * 查询普通用户信息
     * @param userQueryDTO 可以根据用户Id，名称，第几页，每页个数
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO){
        return getTableDataInfo(userService.list(userQueryDTO));
    }

    /**
     * 更新用户状态
     * 传入的是用户Id和需要修改的状态信息
     */
//    TODO 拉黑，限制用户操作    解禁，放开对于用户限制
//    更新数据库中用户状态的信息
    @PutMapping("/updateStatus")
    public R<Void> updateStatus(@RequestBody UserDTO userDTO){
        return toR(userService.updateStatus(userDTO));
    }
}

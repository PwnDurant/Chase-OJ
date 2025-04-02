package com.zqq.friend.controller.user;


import com.zqq.common.core.constants.HttpConstants;
import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.vo.LoginUserVO;
import com.zqq.friend.domain.user.dto.UserDTO;
import com.zqq.friend.domain.user.dto.UserUpdateDTO;
import com.zqq.friend.domain.user.vo.UserVO;
import com.zqq.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    /**
     * 发送验证码
     * @param userDTO
     * @return
     */
    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        return toR(userService.sendCode(userDTO));
    }

    /**
     * 登入注册
     * @param userDTO 传入手机号和验证码
     * @return 如果登入成功返回token
     */
    @PostMapping("/code/login")
    public R<String> codeLogin(@RequestBody UserDTO userDTO){
        return R.ok(userService.codeLogin(userDTO));
    }

    /**
     * 退出登入功能
     * @param token
     * @return
     */
    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return toR(userService.logout(token));
    }

    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return userService.info(token);
    }


    @GetMapping("/detail")
    public R<UserVO> detail() {
        return R.ok(userService.detail());
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.edit(userUpdateDTO));
    }

    @PutMapping("/head-image/update")
    public R<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO) {
        return toR(userService.updateHeadImage(userUpdateDTO.getHeadImage()));
    }
}

package com.zqq.system.test;

import com.zqq.common.core.domain.R;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.redis.service.RedisService;
import com.zqq.system.domain.sysuser.SysUser;
import com.zqq.system.test.domain.TestLoginDTO;
import com.zqq.system.test.domain.VaDTO;
import com.zqq.system.test.service.impl.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

//    test/list 查询tb_test中所有数据
    @RequestMapping("/list")
    public List<?> list(){
        return testService.list();
    }

    @RequestMapping("/add")
    public String add(){
        return testService.add();
    }

    @GetMapping("/apifoxtest")
    public R<String> test(String apiId){
        R<String> result=new R<>();
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setData("apifox"+apiId);
        return result;
    }


    @PostMapping("/apifoxPost")
    public R<String> apifoxPost(@RequestBody TestLoginDTO testLoginDTO){
        R<String> result=new R<>();
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setData("apifox"+testLoginDTO.getUserAccount()+";"+testLoginDTO.getPassword());
        return result;
    }


    @Autowired
    private RedisService redisService;
    @GetMapping("/addRedis")
    public String addRedis(){
        SysUser user=new SysUser();
        user.setUserAccount("redisTest");
        redisService.setCacheObject("u",user);
        SysUser u = redisService.getCacheObject("u", SysUser.class);
        return u.toString();
    }


    @GetMapping("/va")
    public String validation(@Validated VaDTO vaDTO){
        return "参数测试";
    }






}

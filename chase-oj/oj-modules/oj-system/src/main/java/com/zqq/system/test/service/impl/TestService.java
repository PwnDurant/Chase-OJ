package com.zqq.system.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.system.test.domain.TestDomain;
import com.zqq.system.test.mapper.TestMapper;
import com.zqq.system.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService implements ITestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<?> list() {
        return testMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public String add() {
        TestDomain testDomain=new TestDomain();
        testDomain.setTitle("ceshi");
        testDomain.setContent("uuid");
        testMapper.insert(testDomain);
        return "add success!";
    }

}

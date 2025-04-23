package com.zqq.judge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zqq.api.domain.UserExeResult;
import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.constants.JudgeConstants;
import com.zqq.common.core.enums.CodeRunStatus;
import com.zqq.judge.domain.SandBoxExecuteResult;
import com.zqq.judge.domain.UserSubmit;
import com.zqq.judge.mapper.UserSubmitMapper;
import com.zqq.judge.service.IJudgeService;
import com.zqq.judge.service.ISandboxPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 测评服务
 */
@Service
@Slf4j
public class JudgeServiceImpl implements IJudgeService {

    @Autowired
    private ISandboxPoolService sandboxPoolService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;


    /**
     * 进行代码测评
     * @param judgeSubmitDTO 传入的数据
     * @return 返回测评结果
     */
    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        log.info("---- 判题逻辑开始 ----");
        SandBoxExecuteResult sandBoxExecuteResult=sandboxPoolService.exeJavaCode(judgeSubmitDTO.getUserId(),judgeSubmitDTO.getUserCode(),judgeSubmitDTO.getInputList());
        UserQuestionResultVO userQuestionResultVO=new UserQuestionResultVO();
        if(sandBoxExecuteResult!=null&& CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())){
//            说明程序被正确执行了，现在判断执行结果是否正确
            userQuestionResultVO=doJudge(judgeSubmitDTO,sandBoxExecuteResult,userQuestionResultVO);
        }else{
//            说明程序执行失败
            userQuestionResultVO.setPass(Constants.FALSE);
            if(sandBoxExecuteResult!=null){
//                说明有结果但是不正确可能程序执行出异常等等，将错误信息返回给用户
                userQuestionResultVO.setExeMessage(sandBoxExecuteResult.getExeMessage());
            }else{
//                没有结果，未知错误
                userQuestionResultVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            }
//            设置分数
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        saveUserSubmit(judgeSubmitDTO,userQuestionResultVO);
        log.info("判题逻辑结束，判题结果为:{}",userQuestionResultVO);
        return userQuestionResultVO;

    }

    private void saveUserSubmit(JudgeSubmitDTO judgeSubmitDTO, UserQuestionResultVO userQuestionResultVO) {
        UserSubmit userSubmit = new UserSubmit();
        BeanUtil.copyProperties(userQuestionResultVO, userSubmit);
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        userSubmit.setCaseJudgeRes(JSON.toJSONString(userQuestionResultVO.getUserExeResultList()));
        userSubmit.setCreateBy(judgeSubmitDTO.getUserId());
        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>()
                .eq(UserSubmit::getUserId, judgeSubmitDTO.getUserId())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                .isNull(judgeSubmitDTO.getExamId() == null, UserSubmit::getExamId)
                .eq(judgeSubmitDTO.getExamId() != null, UserSubmit::getExamId, judgeSubmitDTO.getExamId()));
        userSubmitMapper.insert(userSubmit);
    }

    //    判读执行结果和预期输出结果是否相同
    private UserQuestionResultVO doJudge(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult, UserQuestionResultVO userQuestionResultVO) {
        List<String> exeOutputList=sandBoxExecuteResult.getOutputList();
        List<String> outputList=judgeSubmitDTO.getOutputList();
        if(outputList.size()!=exeOutputList.size()){
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        List<UserExeResult> userExeResultList=new ArrayList<>();
        boolean passed=resultCompare(judgeSubmitDTO,exeOutputList,outputList,userExeResultList);
        return assembleUserQuestionResultVO(judgeSubmitDTO, sandBoxExecuteResult, userQuestionResultVO, userExeResultList, passed);
    }

    private boolean resultCompare(JudgeSubmitDTO judgeSubmitDTO, List<String> exeOutputList, List<String> outputList, List<UserExeResult> userExeResultList) {
        boolean passed=true;
        for (int index = 0; index < outputList.size(); index++) {
            String output = outputList.get(index);
            String exeOutPut = exeOutputList.get(index);
            String input = judgeSubmitDTO.getInputList().get(index);
            UserExeResult userExeResult = new UserExeResult();
            userExeResult.setInput(input);
            userExeResult.setOutput(output);
            userExeResult.setExeOutput(exeOutPut);
            userExeResultList.add(userExeResult);
            if (!output.equals(exeOutPut)) {
                passed = false;
                log.info("输入：{}， 期望输出：{}， 实际输出：{} ", input, output, exeOutputList);
            }
        }
        return passed;
    }

    private UserQuestionResultVO assembleUserQuestionResultVO(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult, UserQuestionResultVO userQuestionResultVO, List<UserExeResult> userExeResultList, boolean passed) {
        userQuestionResultVO.setUserExeResultList(userExeResultList);
        if (!passed) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        if (sandBoxExecuteResult.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            return userQuestionResultVO;
        }
        if (sandBoxExecuteResult.getUseTime() > judgeSubmitDTO.getTimeLimit()) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            return userQuestionResultVO;
        }
        userQuestionResultVO.setPass(Constants.TRUE);
        int score = judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE;
        userQuestionResultVO.setScore(score);
        return userQuestionResultVO;
    }


}



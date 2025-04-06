package com.zqq.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.zqq.api.RemoteJudgeService;
import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.api.domain.vo.UserQuestionResultVO;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.enums.ProgramType;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.domain.question.Question;
import com.zqq.friend.domain.question.QuestionCase;
import com.zqq.friend.domain.question.es.QuestionES;
import com.zqq.friend.domain.user.dto.UserSubmitDTO;
import com.zqq.friend.elasticsearch.QuestionRepository;
import com.zqq.friend.mapper.question.QuestionMapper;
import com.zqq.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuestionServiceImpl implements IUserQuestionService {


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    public UserQuestionServiceImpl(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
    }

    //    提交代码逻辑
    @Override
    public R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO) {
        if(ProgramType.JAVA.getValue().equals(submitDTO.getProgramType())){
//            按照java逻辑走
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

//    讲传入的数据组织一下再一块传入进行判断
    private JudgeSubmitDTO assembleJudgeSubmitDTO(UserSubmitDTO submitDTO) {

        Long questionId=submitDTO.getQuestionId();
        QuestionES questionES=questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if(questionES!=null){
            BeanUtil.copyProperties(questionES,judgeSubmitDTO);
        }else{
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question,judgeSubmitDTO);
            questionES=new QuestionES();
            BeanUtil.copyProperties(question,questionES);
            questionRepository.save(questionES);
        }
//        包装输入的信息
        judgeSubmitDTO.setUserId(ThreadLocalIUtil.get(Constants.USER_ID, Long.class));
        judgeSubmitDTO.setExamId(submitDTO.getExamId());
        judgeSubmitDTO.setProgramType(submitDTO.getProgramType());
        judgeSubmitDTO.setUserCode(codeConnect(submitDTO.getUserCode(),questionES.getMainFuc()));
//        得到输入输出的值,再次进行包装
        List<QuestionCase> questionCaseList= JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList=questionCaseList.stream().map(QuestionCase::getInput).toList();
        judgeSubmitDTO.setInputList(inputList);
        List<String> outputList=questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setOutputList(outputList);
        return judgeSubmitDTO;
    }

    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter); // 找到最后一个 "}" 的位置
        if (targetLastIndex != -1) {
            // 将 mainFunc 插入到最后一个 "}" 前面
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.FAILED); // 如果找不到 "}"，抛出异常
    }
}

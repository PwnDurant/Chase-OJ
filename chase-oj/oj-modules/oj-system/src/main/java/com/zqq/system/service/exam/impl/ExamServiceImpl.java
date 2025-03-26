package com.zqq.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.system.domain.exam.Exam;
import com.zqq.system.domain.exam.ExamQuestion;
import com.zqq.system.domain.exam.dto.ExamAddDTO;
import com.zqq.system.domain.exam.dto.ExamEditDTO;
import com.zqq.system.domain.exam.dto.ExamQueryDTO;
import com.zqq.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zqq.system.domain.exam.vo.ExamDetailVO;
import com.zqq.system.domain.exam.vo.ExamVO;
import com.zqq.system.domain.question.Question;
import com.zqq.system.domain.question.vo.QuestionVO;
import com.zqq.system.mapper.exam.ExamMapper;
import com.zqq.system.mapper.exam.ExamQuestionMapper;
import com.zqq.system.mapper.question.QuestionMapper;
import com.zqq.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements IExamService {

    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
//        根据传入的竞赛标题，判断是否已经存在相同的竞赛
        checkExamSaveParams(examAddDTO,null);

//        将信息添加到exam数据库中
        Exam exam=new Exam();
        checkExam(exam);
        BeanUtil.copyProperties(examAddDTO,exam);
        examMapper.insert(exam);
        return exam.getExamId().toString();
    }



    @Override
    public boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO) {

//        通过传入的竞赛id判断竞赛是否存在
        Exam exam = getExam(examQuestionAddDTO.getExamId());
        checkExam(exam);
//        判断添加的题目是否存在
        LinkedHashSet<Long> questionIdSet =examQuestionAddDTO.getQuestionIdSet();
        if(CollectionUtil.isEmpty(questionIdSet)){
            return true;
        }
//        判断所传入的题目数量和从数据库中查出来的题目数量是否一样
        List<Question> questionList=questionMapper.selectBatchIds(questionIdSet);
        if(CollectionUtil.isEmpty(questionList)||questionList.size()<questionIdSet.size()){
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }
//        添加题目到竞赛题目表中
        return saveExamQuestion(questionIdSet, exam);
    }

    @Override
    public ExamDetailVO detail(Long examId) {
        ExamDetailVO examDetailVO=new ExamDetailVO();
        Exam exam = getExam(examId);
        BeanUtil.copyProperties(exam,examDetailVO);
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if(CollectionUtil.isEmpty(examQuestionList)){
//            将详情返回,只包含竞赛的基本信息
            return examDetailVO;
        }
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));

        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionList, QuestionVO.class);

        examDetailVO.setExamQuestionList(questionVOList);
        return examDetailVO;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
//        判断
        Exam exam = getExam(examEditDTO.getExamId());
        checkExam(exam);
        checkExamSaveParams(examEditDTO,examEditDTO.getExamId());

        BeanUtil.copyProperties(examEditDTO,exam);
        return examMapper.updateById(exam);
    }

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        ExamQuestion examQuestion = examQuestionMapper.selectOne(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));
        if(examQuestion==null){
            throw new ServiceException(ResultCode.DONT_EXISTS);
        }
        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId,questionId));
    }

    private void checkExam(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }

    private void checkExamSaveParams(ExamAddDTO examSaveDTO,Long examId) {

        List<Exam> examList=examMapper
                .selectList(new LambdaQueryWrapper<Exam>().eq(Exam::getTitle, examSaveDTO.getTitle())
                        .ne(examId!=null,Exam::getExamId,examId));
        if(CollectionUtil.isNotEmpty(examList)){
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }

//        判断传入的时间是否合理
        if(examSaveDTO.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if(examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())){
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
    }


    private boolean saveExamQuestion(LinkedHashSet<Long> questionIdSet, Exam exam) {
//        在插入之前判断是否有一样的题目已经在竞赛中
//        List<ExamQuestion> examQuestionList1 = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
//                .eq(ExamQuestion::getExamId, exam.getExamId()));
//        for (ExamQuestion examQuestion : examQuestionList1) {
//            if(questionIdSet.contains(examQuestion.getQuestionId())){
//                throw new ServiceException(ResultCode.EXAM_QUESTION_EXISTS);
//            }
//        }

        List<Long> questionIds = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                        .select(ExamQuestion::getQuestionId)
                        .eq(ExamQuestion::getExamId, exam.getExamId()))
                .stream()
                .map(ExamQuestion::getQuestionId).toList();

        Judge(questionIdSet, questionIds);

        int num=1;
        List<ExamQuestion> examQuestionList=new ArrayList<>();
        for (Long questionId : questionIdSet) {
            ExamQuestion examQuestion=new ExamQuestion();
            examQuestion.setExamId(exam.getExamId());
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(num++);
            examQuestionList.add(examQuestion);
        }
        return saveBatch(examQuestionList);
    }

    private static void Judge(LinkedHashSet<Long> questionIdSet, List<Long> questionIds) {
        for (Long questionId : questionIds) {
            if(questionIdSet.contains(questionId)){
                throw new ServiceException(ResultCode.EXAM_QUESTION_EXISTS);
            }
        }
    }

    private Exam getExam(Long examId) {
        Exam exam =examMapper.selectById(examId);
        if(exam==null){
            throw new ServiceException(ResultCode.EXAM_NOT_EXISTS);
        }
        return exam;
    }


}

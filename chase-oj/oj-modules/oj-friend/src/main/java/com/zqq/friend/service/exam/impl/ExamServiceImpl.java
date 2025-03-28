package com.zqq.friend.service.exam.impl;

import com.github.pagehelper.PageHelper;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }
}

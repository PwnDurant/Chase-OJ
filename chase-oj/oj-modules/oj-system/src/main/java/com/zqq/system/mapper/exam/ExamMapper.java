package com.zqq.system.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.system.domain.exam.Exam;
import com.zqq.system.domain.exam.dto.ExamQueryDTO;
import com.zqq.system.domain.exam.vo.ExamVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//继承了BaseMapper，自动就被注册了
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}

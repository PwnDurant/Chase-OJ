package com.zqq.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.manage.ExamCacheManager;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.vo.ExamVO;
import com.zqq.friend.mapper.exam.ExamMapper;
import com.zqq.friend.mapper.user.UserExamMapper;
import com.zqq.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private ExamCacheManager examCacheManager;
    @Autowired
    private UserExamMapper userExamMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long listSize = examCacheManager.getListSize(examQueryDTO.getType(),null);
        List<ExamVO> examVOList = null;
        if(listSize==null||listSize==0){
            examVOList=list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
            listSize=new PageInfo<>(examVOList).getTotal();
        }else{
            examCacheManager.getExamVOList(examQueryDTO,null);
            listSize = examCacheManager.getListSize(examQueryDTO.getType(),null);
        }

        if(CollectionUtil.isEmpty(examVOList)){
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList,listSize);

    }
}

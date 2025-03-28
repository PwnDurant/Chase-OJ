package com.zqq.friend.controller.exam;


import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;

    /**
     * 显示竞赛列表
     * @param examQueryDTO 传入的是：标题，开始时间，结束时间，第几页（1），每页个数（10）
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return getTableDataInfo(examService.list(examQueryDTO));
    }


}

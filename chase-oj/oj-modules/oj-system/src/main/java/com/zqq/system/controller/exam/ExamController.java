package com.zqq.system.controller.exam;


import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.system.domain.exam.dto.ExamAddDTO;
import com.zqq.system.domain.exam.dto.ExamEditDTO;
import com.zqq.system.domain.exam.dto.ExamQueryDTO;
import com.zqq.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zqq.system.domain.exam.vo.ExamDetailVO;
import com.zqq.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加竞赛基本信息
     * @param examAddDTO 竞赛标题，开始时间，结束时间
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ExamAddDTO examAddDTO){
        return R.ok(examService.add(examAddDTO));
    }

    /**
     * 在竞赛中添加题目
     * @param examQuestionAddDTO 竞赛Id，题目Id
     * @return
     */
    @PostMapping("/question/add")
    public R<Void> questionAdd(@RequestBody ExamQuestionAddDTO examQuestionAddDTO){
        return toR(examService.questionAdd(examQuestionAddDTO));
    }

    /**
     * 删除竞赛中的题目
     * @param examId 竞赛Id
     * @param questionId 题目Id
     * @return
     */
    @DeleteMapping("/question/delete")
    public R<Void> questionDelete(Long examId,Long questionId){
        return toR(examService.questionDelete(examId,questionId));
    }

    /**
     * 显示竞赛详情
     * @param examId 传入竞赛Id
     * @return
     */
    @GetMapping("/detail")
    public R<ExamDetailVO> detail(Long examId){
        return R.ok(examService.detail(examId));
    }

    /**
     * 编辑基本信息
     * @param examEditDTO 需要修改的基本信息
     * @return
     */
    @PutMapping("/edit")
    public R<Void> edit(@RequestBody ExamEditDTO examEditDTO){
        return toR(examService.edit(examEditDTO));
    }

    /**
     * 删除竞赛
     * @param examId 竞赛Id
     * @return
     */
    @DeleteMapping("/delete")
    public R<Void> delete(Long examId){
        return toR(examService.delete(examId));
    }

    /**
     * 发布竞赛
     */
    @PutMapping("/publish")
    public R<Void> publish(Long examId){
        return toR(examService.publish(examId));
    }

    /**
     * 撤销竞赛发布
     */
    @PutMapping("/cancelPublish")
    public R<Void> cancelPublish(Long examId){
        return toR(examService.cancelPublish(examId));
    }





}

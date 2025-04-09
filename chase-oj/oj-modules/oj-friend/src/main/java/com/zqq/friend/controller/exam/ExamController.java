package com.zqq.friend.controller.exam;


import com.zqq.common.core.controller.BaseController;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.friend.domain.exam.dto.ExamQueryDTO;
import com.zqq.friend.domain.exam.dto.ExamRankDTO;
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
    @GetMapping("/semiLogin/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO){
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    /**
     * 显示竞赛列表(redis)
     * @param examQueryDTO 传入的是：标题，开始时间，结束时间，第几页（1），每页个数（10）
     * @return
     */
    @GetMapping("/semiLogin/redis/list")
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO){
        return examService.redisList(examQueryDTO);
    }

    /**
     * 得到竞赛列表中的第一题
     * @param examId 竞赛Id
     * @return 得到竞赛列表中的第一题
     */
    @GetMapping("/getFirstQuestion")
    public R<String> getFirstQuestion(Long examId){
//        逻辑：获取竞赛中题目顺序列表    先从redis redis中没有数据就查询数据库。list 数据类型  key: e:q:l:examId  value: questionId
//        把排在第一个的题目返回给前端
        return R.ok(examService.getFirstQuestion(examId));
    }

    /**
     * 用户排名
     * @param examRankDTO 传入竞赛Id
     * @return 返回排名结果
     */
    @GetMapping("/rank/list")
    public TableDataInfo rankList(ExamRankDTO examRankDTO){
        return examService.rankList(examRankDTO);
    }

    /**
     * 题目的顺序列表 当前题目：questionId
     * redis list 数据类型: key: q:l   value: questionId
     * @param questionId 题目Id
     * @return 前一题
     */
    @GetMapping("/preQuestion")
    public R<String> preQuestion(Long questionId,Long examId){
        return R.ok(examService.preQuestion(questionId,examId));
    }

    /**
     * 题目的顺序列表 当前题目：questionId
     * redis list 数据类型: key: q:l   value: questionId
     * @param questionId 题目Id
     * @return 后一题
     */
    @GetMapping("/nextQuestion")
    public R<String> nextQuestion(Long questionId,Long examId){
        return R.ok(examService. nextQuestion(questionId,examId));
    }



}

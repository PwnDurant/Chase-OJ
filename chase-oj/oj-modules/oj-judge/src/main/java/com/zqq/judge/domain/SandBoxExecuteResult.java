package com.zqq.judge.domain;

import com.zqq.common.core.enums.CodeRunStatus;
import lombok.Data;

import java.util.List;

//代码沙箱返回结果
@Data
public class SandBoxExecuteResult {

    private CodeRunStatus runStatus;  //执行结果

    private String exeMessage;   //异常信息

    private List<String> outputList; //执行结果

    private Long useMemory;  //占用内存  kb

    private Long useTime;   //消耗时间   ms

    /**
     * 已知状态码和错误信息
     * @param runStatus 状态信息
     * @param errorMsg 具体错误信息
     * @return 返回失败的信息
     */
    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, String errorMsg) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(errorMsg);
        return result;
    }

    /**
     * 已知状态码返回信息
     * @param runStatus 状态码
     * @return 返回规定好的状态信息
     */
    public static SandBoxExecuteResult fail(CodeRunStatus runStatus) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(runStatus.getMsg());
        return result;
    }

    /**
     * 知道更具体的失败信息
     * @param runStatus 状态码
     * @param outputList 实际输出结果
     * @param useMemory 用户所占空间
     * @param useTime 用户所花时间
     * @return 返回更具体的信息
     */
    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, List<String> outputList,
                                            Long useMemory, Long useTime) {
        SandBoxExecuteResult result = fail(runStatus);
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }

    /**
     * 返回成功信息
     * @param runStatus 运行状态
     * @param outputList 实际输出
     * @param useMemory 用户所花空间
     * @param useTime 用户所使用空间
     * @return 返回具体成功信息
     */
    public static SandBoxExecuteResult success(CodeRunStatus runStatus, List<String> outputList,
                                               Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }
}


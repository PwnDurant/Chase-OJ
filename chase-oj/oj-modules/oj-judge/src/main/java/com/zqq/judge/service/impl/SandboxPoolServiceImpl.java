package com.zqq.judge.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.constants.JudgeConstants;
import com.zqq.common.core.enums.CodeRunStatus;
import com.zqq.judge.callback.DockerStartResultCallback;
import com.zqq.judge.callback.StatisticsCallback;
import com.zqq.judge.config.DockerSandBoxPool;
import com.zqq.judge.domain.CompileResult;
import com.zqq.judge.domain.SandBoxExecuteResult;
import com.zqq.judge.service.ISandboxPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 沙箱池服务
 */
@Service
@Slf4j
public class SandboxPoolServiceImpl implements ISandboxPoolService {

    @Autowired
    private DockerSandBoxPool sandBoxPool;

    @Autowired
    private DockerClient dockerClient;

    private String containerId;

    private String userCodeFileName;

    @Value("${sandBox.limit.time:5}")
    private Long timeLimit;

    /**
     * 执行传过来的代码
     * @param userId 用户id
     * @param userCode 用户代码
     * @param inputList 实际输入
     * @return
     */
    @Override
    public SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList) {
        containerId= sandBoxPool.getContainer();
        createUserCodeFile(userCode);
//        编译代码
        CompileResult compileResult = compileCodeByDocker();
//        编译失败
        if(!compileResult.isCompiled()){
            sandBoxPool.returnContainer(containerId); //归还容器
            deleteUserCodeFile(); //删除对应文件
            return SandBoxExecuteResult.fail(CodeRunStatus.COMPILE_FAILED,compileResult.getExeMessage()); //返回错误信息
        }
//        编译成功，执行代码
        return executeJavaCodeByDocker(inputList);
    }


    //    删除对应代码文件
    private void deleteUserCodeFile() {
        FileUtil.del(userCodeFileName);
    }


    //    创建并返回用户代码的文件
    private void createUserCodeFile(String userCode) {
        String codeDir= sandBoxPool.getCodeDir(containerId);
        log.info("user-pool路径信息：{}", codeDir);
        userCodeFileName = codeDir + File.separator + JudgeConstants.USER_CODE_JAVA_CLASS_NAME;
        if(FileUtil.exist(userCodeFileName)){  //如果文件之前存在，就删除之前的文件
            FileUtil.del(userCodeFileName);
        }
        FileUtil.writeString(userCode,userCodeFileName, Constants.UTF8); //向指定文件中写入指定字符集的数据
    }

    //     使用docker进行编译
    private CompileResult compileCodeByDocker() {
        String cmdId=createExecCmd(JudgeConstants.DOCKER_JAVAC_CMD,null,containerId); //创建了docker执行命令
        DockerStartResultCallback resultCallback = new DockerStartResultCallback(); //初始化回掉对象（如输出，错误，状态)
        CompileResult compileResult = new CompileResult(); //初始化编译结果对象
        try{
            dockerClient.execStartCmd(cmdId)
                    .exec(resultCallback)
                    .awaitCompletion();
            if(CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())){
                compileResult.setCompiled(false);
                compileResult.setExeMessage(resultCallback.getErrorMessage());
            } else {
                compileResult.setCompiled(true);
            }
            return compileResult;
        }catch (InterruptedException e){
            throw new RuntimeException(e); ////此处可以直接抛出 已做统一异常处理  也可再做定制化处理
        }

    }

//    创建命令
    private String createExecCmd(String[] javaCmdArr, String inputArgs, String containerId) {
        if (!StrUtil.isEmpty(inputArgs)) {
            //当入参不为空时拼接入参
            String[] inputArray = inputArgs.split(" "); //入参
            javaCmdArr = ArrayUtil.append(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArray);
        }
        ExecCreateCmdResponse cmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(javaCmdArr)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        return cmdResponse.getId();
    }

//    编译通过开始执行代码
    private SandBoxExecuteResult executeJavaCodeByDocker(List<String> inputList) {
        ArrayList<String> outList = new ArrayList<>();
        long maxMemory = 0L;  //最大占用内存
        long maxUseTime = 0L; //最大运行时间
//        执行用户代码
        for(String inputArgs : inputList){
            String cmdId=createExecCmd(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArgs, containerId);
            StopWatch stopWatch = new StopWatch(); //开始执行代码 执行完代码后开始计时
            StatsCmd statsCmd = dockerClient.statsCmd(containerId); //执行情况监控 启动监控器
            StatisticsCallback statisticsCallback = statsCmd.exec(new StatisticsCallback());
            stopWatch.start();
            DockerStartResultCallback resultCallback = new DockerStartResultCallback();
            try{
                dockerClient.execStartCmd(cmdId)
                        .exec(resultCallback)
                        .awaitCompletion(timeLimit, TimeUnit.SECONDS);
                if(CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())){
                    //未通过所有用例返回结果
                    return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED);
                }
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            stopWatch.stop();  //结束时间统计
            statsCmd.close();  //结束docker容器执行统计
            statsCmd.close();  //结束docker容器执行统计
            long userTime = stopWatch.getLastTaskTimeMillis(); //执行耗时
            maxUseTime = Math.max(userTime, maxUseTime);       //记录最大的执行用例耗时
            Long memory = statisticsCallback.getMaxMemory();
            if (memory != null) {
                maxMemory = Math.max(maxMemory, statisticsCallback.getMaxMemory()); //记录最大的执行用例占用内存
            }
            outList.add(resultCallback.getMessage().trim());
        }
        sandBoxPool.returnContainer(containerId);
        deleteUserCodeFile(); //清理文件

        return getSanBoxResult(inputList, outList, maxMemory, maxUseTime); //封装结果
    }

//    封装结果
    private SandBoxExecuteResult getSanBoxResult(List<String> inputList, ArrayList<String> outList, long maxMemory, long maxUseTime) {
        if (inputList.size() != outList.size()) {
            //输入用例数量 不等于 输出用例数量  属于执行异常
            return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED, outList, maxMemory, maxUseTime);
        }
        return SandBoxExecuteResult.success(CodeRunStatus.SUCCEED, outList, maxMemory, maxUseTime);
    }
}

package com.zqq.judge.callback;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zqq.common.core.enums.CodeRunStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义Docker命令执行回调类
 */
@Getter
@Setter
@Slf4j
public class DockerStartResultCallback extends ExecStartResultCallback {

    private CodeRunStatus codeRunStatus;  //记录执行成功还是失败

    private String errorMessage;  //错误信息

    private String message;  //信息

    /**
     * 重写回掉函数
     * @param frame docker执行所输出的内容
     */
    @Override
    public void onNext(Frame frame) {
        StreamType streamType = frame.getStreamType();
        if (StreamType.STDERR.equals(streamType)) {
            if (StrUtil.isEmpty(errorMessage)) {
                errorMessage = new String(frame.getPayload());
            } else {
                errorMessage = errorMessage + new String(frame.getPayload());
            }
            codeRunStatus = CodeRunStatus.FAILED;
        } else {
            String msgTmp = new String(frame.getPayload());
            if (StrUtil.isNotEmpty(msgTmp)) {
                message = new String(frame.getPayload());
            }
            codeRunStatus = CodeRunStatus.SUCCEED;
        }
        super.onNext(frame);
    }
}


package com.zqq.judge.rabbit;


import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.common.core.constants.RabbitMQConstants;
import com.zqq.judge.service.IJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消费者代码
 */
@Slf4j
@Component
public class JudgeConsumer {

    @Autowired
    private IJudgeService judgeService;

    @RabbitListener(queues = RabbitMQConstants.OJ_WORK_QUEUE)
    public void consume(JudgeSubmitDTO judgeSubmitDTO){
        log.info("收到消息为: {}", judgeSubmitDTO);
        try{
            judgeService.doJudgeJavaCode(judgeSubmitDTO);
        }catch (Exception e){
            log.error("消息处理失败",e);
        }
    }

}

package com.zqq.friend.rabbit;


import com.zqq.api.domain.dto.JudgeSubmitDTO;
import com.zqq.common.core.constants.RabbitMQConstants;
import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.security.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq生产者代码
 */
@Component
@Slf4j
public class JudgeProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void produceMsg(JudgeSubmitDTO judgeSubmitDTO){
        try{
            rabbitTemplate.convertAndSend(RabbitMQConstants.OJ_WORK_QUEUE,judgeSubmitDTO);
        }catch (Exception e){
            log.error("生产者发送消息异常", e);
            throw new ServiceException(ResultCode.FAILED_RABBIT_PRODUCE);
        }
    }

}

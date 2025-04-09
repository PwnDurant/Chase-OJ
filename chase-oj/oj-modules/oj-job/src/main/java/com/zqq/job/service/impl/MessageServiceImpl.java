package com.zqq.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.job.domain.message.Message;
import com.zqq.job.mapper.message.MessageMapper;
import com.zqq.job.service.IMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper,Message> implements IMessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}

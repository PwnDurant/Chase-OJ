package com.zqq.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqq.job.domain.message.MessageText;
import com.zqq.job.mapper.message.MessageTextMapper;
import com.zqq.job.service.IMessageTextService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText> implements IMessageTextService {


    @Override
    public boolean batchInsert(List<MessageText> messageTextList) {
        return saveBatch(messageTextList);
    }
}

package com.zqq.job.service;

import com.zqq.job.domain.message.Message;

import java.util.List;

public interface IMessageService {
    boolean batchInsert(List<Message> messageList);
}

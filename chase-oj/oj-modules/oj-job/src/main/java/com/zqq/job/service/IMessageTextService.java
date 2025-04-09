package com.zqq.job.service;

import com.zqq.job.domain.message.MessageText;

import java.util.List;

public interface IMessageTextService {


    boolean batchInsert(List<MessageText> messageTextList);
}

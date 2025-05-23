package com.zqq.friend.mapper.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zqq.friend.domain.message.MessageText;
import com.zqq.friend.domain.message.vo.MessageTextVO;

import java.util.List;


public interface MessageTextMapper extends BaseMapper<MessageText> {

    List<MessageTextVO> selectUserMsgList(Long userId);

}

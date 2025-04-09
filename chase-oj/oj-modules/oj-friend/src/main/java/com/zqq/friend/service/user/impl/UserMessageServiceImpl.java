package com.zqq.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zqq.common.core.constants.Constants;
import com.zqq.common.core.domain.PageQueryDTO;
import com.zqq.common.core.domain.TableDataInfo;
import com.zqq.common.core.utils.ThreadLocalIUtil;
import com.zqq.friend.domain.message.vo.MessageTextVO;
import com.zqq.friend.manager.MessageCacheManager;
import com.zqq.friend.mapper.message.MessageTextMapper;
import com.zqq.friend.service.user.IUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMessageServiceImpl implements IUserMessageService {

    @Autowired
    private MessageCacheManager messageCacheManager;

    @Autowired
    private MessageTextMapper messageTextMapper;

    @Override
    public TableDataInfo list(PageQueryDTO dto) {
        Long userId= ThreadLocalIUtil.get(Constants.USER_ID, Long.class);
        Long total=messageCacheManager.getListSize(userId);
        List<MessageTextVO> messageTextVOList;
        if(total==null||total<=0){
//            从数据库中查询竞赛列表
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
            messageTextVOList=messageTextMapper.selectUserMsgList(userId);
            messageCacheManager.refreshCache(userId);
            total=new PageInfo<>(messageTextVOList).getTotal();
        }else{
            messageTextVOList=messageCacheManager.getMsgTextVOList(dto,userId);
        }
        if(CollectionUtil.isEmpty(messageTextVOList)){
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(messageTextVOList,total);
    }
}

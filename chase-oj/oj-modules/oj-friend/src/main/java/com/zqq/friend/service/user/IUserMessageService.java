package com.zqq.friend.service.user;

import com.zqq.common.core.domain.PageQueryDTO;
import com.zqq.common.core.domain.TableDataInfo;

public interface IUserMessageService {
    TableDataInfo list(PageQueryDTO dto);
}

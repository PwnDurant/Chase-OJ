package com.zqq.friend.service.file.impl;

import com.zqq.common.core.enums.ResultCode;
import com.zqq.common.file.domain.OSSResult;
import com.zqq.common.file.service.OSSService;
import com.zqq.common.security.exception.ServiceException;
import com.zqq.friend.service.file.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    @Autowired
    private OSSService ossService;

    @Override
    public OSSResult upload(MultipartFile file) {
        try {
            return ossService.uploadFile(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(ResultCode.FAILED_FILE_UPLOAD);
        }
    }
}

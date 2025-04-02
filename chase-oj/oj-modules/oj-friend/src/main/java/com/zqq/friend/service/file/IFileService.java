package com.zqq.friend.service.file;

import com.zqq.common.file.domain.OSSResult;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    OSSResult upload(MultipartFile file);
}

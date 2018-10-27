package com.mmall.service;

import com.mmall.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-27 13:49
 **/
public interface IFileService {
    ServerResponse<String> upload(MultipartFile file, String path);
}

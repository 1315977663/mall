package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @program: mmall
 * @description:
 * @author: fbl
 * @create: 2018-10-27 13:50
 **/
@Service("iFileService")
public class FileService implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileService.class);

    @Override
    public ServerResponse<String> upload(MultipartFile file, String path){

        // 文件原始名称
        String originalFilename = file.getOriginalFilename();

        // 文件后缀名,包括点
        String fileExtensionName = originalFilename.substring(originalFilename.lastIndexOf('.'));

        // 上传的文件名
        String fileUploadName = UUID.randomUUID().toString() + fileExtensionName;

        logger.info("文件开始上传。上传文件名：{},上传路径：{},上传后新文件名{}", originalFilename, path, fileUploadName);


        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true); // 允许写的权限
            if (fileDir.mkdirs()){
                logger.info("文件夹创建成功：{}", fileDir.getAbsolutePath());
            } else {
                logger.error("文件夹创建失败");
                return ServerResponse.createByErrorMessage("文件夹创建失败");
            }
        }

        File targetFile = new File(path, fileUploadName);

        try {
            file.transferTo(targetFile);
            // 文件已经传输到了服务器上


            //todo 传到ftp服务上

            // targetFile.delete();
            //删除服务器上文件
        } catch (IOException e) {
            logger.error("文件上传失败", e.getMessage());
            return ServerResponse.createByErrorMessage("文件上传失败");
        }


        return ServerResponse.createBySuccess(targetFile.getName(), "文件上传成功");
    }

}

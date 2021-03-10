package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fullFileName = file.getOriginalFilename();
        int dotPos = fullFileName.lastIndexOf(".");
        String fileExtensionName = dotPos >=0 ? fullFileName.substring(dotPos + 1) : "";
        String fileName = dotPos >= 0 ? fullFileName.substring(0, dotPos) : fullFileName;
        String serverFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("Upload file name {}, path {}, server file name {}", fileName, path, serverFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, serverFileName);

        try {
            file.transferTo(targetFile);
            FtpUtil.uploadFiles(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (Exception e) {
            logger.error("file upload exception", e);
            return null;
        }
        return targetFile.getName();
    }
}

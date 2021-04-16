package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {
    public String upload(MultipartFile file, String path) {
        String fullFileName = file.getOriginalFilename();
        int dotPos = fullFileName.lastIndexOf(".");
        String fileExtensionName = dotPos >= 0 ? fullFileName.substring(dotPos + 1) : "";
        String fileName = dotPos >= 0 ? fullFileName.substring(0, dotPos) : fullFileName;
        String serverFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        log.info("Upload file name {}, path {}, server file name {}", fileName, path, serverFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, serverFileName);
        String targetFileName = targetFile.getName();;
        try {
            file.transferTo(targetFile);
            FtpUtil.uploadFiles(Lists.newArrayList(targetFile));
        } catch (Exception e) {
            log.error("file upload exception", e);
            targetFileName = null;
        } finally {
            targetFile.delete();
        }
        return targetFileName;
    }
}

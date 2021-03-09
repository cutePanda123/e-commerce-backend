package com.mmall.service.impl;

import com.mmall.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

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

            // ToDo: upload file to FTP server, then delete the file in web server.
        } catch (Exception e) {
            logger.error("file upload exception", e);
            return null;
        }
        return targetFile.getName();
    }
}

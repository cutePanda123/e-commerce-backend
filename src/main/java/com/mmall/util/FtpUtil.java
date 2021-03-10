package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FtpUtil {
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUsername = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    public FtpUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.password = password;
        this.username = username;
        this.port = port;
    }

    public static boolean uploadFiles(List<File> files) throws IOException {
        FtpUtil ftpUtil = new FtpUtil(ftpIp, 21, ftpUsername, ftpPassword);
        logger.info("connecting to ftp server");
        boolean result = ftpUtil.uploadFiles("img-folder", files);
        logger.info("finished uploading with result {}", result);
        return result;
    }

    private boolean uploadFiles(String ftpServerRemotePath, List<File> files) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;

        if (connectToFtpServer(this.ip, this.port, this.username, this.password)) {
            try {
                ftpClient.changeWorkingDirectory(ftpServerRemotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : files) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }
            } catch (Exception e) {
                uploaded = false;
                logger.error("file upload ftp server exception", e);
                e.printStackTrace();;
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectToFtpServer(String ip, int port, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(username, password);
        } catch (Exception e) {
            logger.error("ftp server connection exception", e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String username;
    private String password;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}

package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by geely
 */
public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getValue("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getValue("ftp.user");
    private static Integer ftpPort = Integer.valueOf(Objects.requireNonNull(PropertiesUtil.getValue("ftp.port")));
    private static String ftpPass = PropertiesUtil.getValue("ftp.pass");

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }
    public static boolean uploadFile(List<File> fileList){
        FTPUtil ftpUtil = new FTPUtil(ftpIp,ftpPort,ftpUser,ftpPass);
        return ftpUtil.uploadFile("img",fileList);
    }


    private boolean uploadFile(String remotePath,List<File> fileList){
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath); // 使用ftp服务器下指定的文件夹
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                    fis.close();
                }
                uploaded = true;
                logger.info("文件上传到ftp服务器成功");
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        logger.error("连接关闭失败{}",e);
                    }
                }
            }
        }
        return uploaded;
    }



    private boolean connectServer(String ip,int port,String user,String pwd){
        logger.info("ftp连接ip:{},端口:{},用户名:{},密码:{}", ip, port, user, pwd);
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip, port);
            boolean isSuccess = ftpClient.login(user,pwd);
            if(!isSuccess){
                logger.error("ftp用户密码错误");
                return false;
            } else {
                logger.info("ftp服务器连接成功");
                return true;
            }
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
            return false;
        }
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

}

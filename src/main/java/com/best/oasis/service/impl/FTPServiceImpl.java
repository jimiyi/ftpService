package com.best.oasis.service.impl;

import com.best.oasis.ftp.FTPClientPool;
import com.best.oasis.service.FTPService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FTP服务类
 *
 * @author yiwei
 * @create 2017/8/22
 */
@Service("FTPService")
public class FTPServiceImpl implements FTPService {
    private static Logger logger = LoggerFactory.getLogger(FTPServiceImpl.class.getCanonicalName());

    // 本地字符编码
    private static String LOCAL_CHARSET = "UTF-8";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    @Autowired
    FTPClientPool ftpClientPool;

    @Override
    public boolean upload(String fileName,MultipartFile[] multipartFiles, String filePath) throws Exception {
        boolean flag = true;
        FTPClient ftpClient = ftpClientPool.borrowClient();
        if (!ftpClient.changeWorkingDirectory(filePath)) {
            //如果目录不存在创建目录
            String tempPath = "";
            String[] dirs = filePath.split("/");
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) continue;
                tempPath += "/" + dir;
                if (!ftpClient.changeWorkingDirectory(tempPath)) {
                    if (!ftpClient.makeDirectory(tempPath)) {
                        return false;
                    } else {
                        ftpClient.changeWorkingDirectory(tempPath);
                    }
                }
            }
        }
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("utf-8");
        if (multipartFiles != null && multipartFiles.length > 0) {
            for (int i = 0; i < multipartFiles.length; i++) {
                MultipartFile file = multipartFiles[i];
                boolean result = saveFile(fileName,file, ftpClient);
                if(!result)
                    flag = false;
            }
        }
        ftpClientPool.returnClient(ftpClient);
        return flag;
    }

    @Override
    public boolean download(HttpServletResponse response, String fileName, String path) throws Exception {
        boolean flag = false;
        String transferName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("multipart/form-data;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + transferName + "\"");
        FTPClient client = ftpClientPool.borrowClient();
        client.setFileType(FTPClient.BINARY_FILE_TYPE);
        client.changeWorkingDirectory(path);
        FTPFile[] fs = client.listFiles();
        for (FTPFile ff : fs) {
            if (ff.getName().equals(fileName)) {
                OutputStream os = response.getOutputStream();
                flag = client.retrieveFile(new String(ff.getName().getBytes(LOCAL_CHARSET), SERVER_CHARSET), os);
                os.flush();
                os.close();
                break;
            }
        }
        ftpClientPool.returnClient(client);
        return flag;
    }

    private boolean saveFile(String fileName,MultipartFile file, FTPClient client) {
        boolean success = false;
        InputStream inStream = null;
        try {
            String transferName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            inStream = file.getInputStream();
            success = client.storeFile(transferName, inStream);
            if (success == true) {
                return success;
            }
        } catch (Exception e) {
            logger.error("保存文件出错",e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error("关闭输入流出错",e);
                }
            }
        }
        return success;
    }

    @Override
    public boolean append(String remoteFile, InputStream local) throws Exception {
        boolean success = false;
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("utf-8");
            String transferName = new String(remoteFile.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            success = ftpClient.appendFile(transferName,local);
            if (success == true) {
                return success;
            }
        } catch (Exception e) {
            logger.error("追加文件内容出错",e);
        } finally {
            if (local != null) {
                try {
                    local.close();
                } catch (IOException e) {
                    logger.error("关闭输入流出错",e);
                }
            }
            ftpClientPool.returnClient(ftpClient);
        }
        return success;
    }

    @Override
    public boolean delete(String remoteFile) throws Exception {
        boolean success = false;
        FTPClient ftpClient = ftpClientPool.borrowClient();
        try {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("utf-8");
            String transferName = new String(remoteFile.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            success = ftpClient.deleteFile(transferName);
            if (success == true) {
                return success;
            }
        } catch (Exception e) {
            logger.error("删除文件出错",e);
        } finally {
            ftpClientPool.returnClient(ftpClient);
        }

        return success;
    }
}

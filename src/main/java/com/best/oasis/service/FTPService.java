package com.best.oasis.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public interface FTPService {
    /**
     * 上传文件至ftp服务器
     *
     * @param multipartFiles
     * @param filePath
     * @return
     */
    boolean upload(String fileName, MultipartFile[] multipartFiles, String filePath) throws Exception;

    /**
     * 从ftp文件服务器下载文件
     *
     * @param fileName
     * @param path
     * @return
     */
    boolean download(HttpServletResponse response, String fileName, String path) throws Exception;

    /**
     * 追加内容到ftp服务器上文件的末尾
     * @param remoteFile
     * @param local
     * @return
     * @throws Exception
     */
    boolean append(String remoteFile, InputStream local) throws Exception;

    /**
     * 删除ftp服务器上的文件
     * @param remoteFile
     * @return
     * @throws Exception
     */
    boolean delete(String remoteFile) throws Exception;
}

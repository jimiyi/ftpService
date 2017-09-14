package com.best.oasis.controller;

import com.best.oasis.service.FTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 对外提供rest服务的接口
 *
 * @author yiwei
 * @create 2017/8/22
 */
@RestController
public class BestFileController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(BestFileController.class.getCanonicalName());

    @Autowired
    FTPService ftpService;

    /**
     * 上传文件至ftp服务器
     * @param storePath
     * @param uploadFiles
     * @param uploadFileName
     * @return
     */
    @RequestMapping(value = "uploadFile")
    public Map<String, Object> uploadFile(@RequestParam("storePath") String storePath, @RequestParam("file") MultipartFile[] uploadFiles
            , @RequestParam(value = "uploadFileName") String uploadFileName) {
        if (uploadFiles != null && uploadFiles.length > 0) {
            try {
                boolean result = ftpService.upload(uploadFileName, uploadFiles, storePath);
                if (result) {
                    return resultMap("success", uploadFiles.length, "", "上传文件成功。");
                }
            } catch (Exception e) {
                logger.error("上传文件至ftp服务器错误", e);
                return resultMap("error", 0, null, e.getMessage());
            }
            return resultMap("error", 0, 0, "未知错误，文件上传失败");
        } else {
            return resultMap("warning", 0, 0, "文件列表为空");
        }
    }

    /**
     * 追加文件内容到远程ftp上的文件
     * @param remoteFile
     * @param localFile
     * @return
     */
    @RequestMapping(value = "appendFile")
    public Map<String, Object> appendFile(@RequestParam(value = "remoteFile") String remoteFile, @RequestParam("local") MultipartFile localFile) {
        try {
            boolean flag = ftpService.append(remoteFile, localFile.getInputStream());
            if (flag) {
                return resultMap("success", 1, "", "追加文件内容成功。");
            }else
                return resultMap("error", 0, 0, "未知错误，追加文件内容失败");

        } catch (Exception e) {
            logger.error("追加文件<" + remoteFile + ">内容失败", e);
            return resultMap("error", 0, null, e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param path
     * @param fileName
     * @param response
     */
    @RequestMapping(value = "downloadFile")
    public void downloadFile(@RequestParam("path") String path, @RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            boolean result = ftpService.download(response, fileName, path);
            if (!result) {
                logger.error("下载文件<" + fileName + ">失败,文件不存在");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "下载文件失败，文件目录不存在或文件不存在。");
            }
        } catch (Exception e) {
            logger.error("下载文件<" + fileName + ">失败", e);
        }
    }

    /**
     * 删除文件
     * @param remoteFile
     * @return
     */
    @RequestMapping(value = "deleteFile")
    public Map<String, Object> deleteFile(@RequestParam(value = "remoteFile") String remoteFile){
        try {
            boolean result = ftpService.delete(remoteFile);
            if(result){
                return resultMap("success", 1, "", "删除文件"+ remoteFile +"成功。");
            }else
                return resultMap("error", 0, null, "未知错误，删除文件"+ remoteFile +"失败。");
        }catch (Exception e){
            logger.error("删除文件<" + remoteFile + ">失败", e);
            return resultMap("error", 0, null, e.getMessage());
        }
    }

    @RequestMapping(value = "test")
    public ModelAndView test() {
        return new ModelAndView("test");
    }
}

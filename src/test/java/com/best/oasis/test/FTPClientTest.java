package com.best.oasis.test;

import com.best.oasis.ftp.FTPClientConfig;
import com.best.oasis.ftp.FTPClientFactory;
import com.best.oasis.ftp.FTPClientPool;
import com.best.oasis.util.ConfigFileUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * ftp工具类测试
 *
 * @author yiwei
 * @create 2017/8/21
 */
public class FTPClientTest {
    private FTPClient ftpClient;
    private Logger log = LoggerFactory.getLogger(FTPClientTest.class.getCanonicalName());

    @Test
    public void test() throws Exception {
        FTPClientConfig config = new FTPClientConfig();
        config.setHost(ConfigFileUtil.getValue("ftp.address"));
        config.setPort(Integer.parseInt(ConfigFileUtil.getValue("ftp.port")));
        config.setUsername(ConfigFileUtil.getValue("ftp.username"));
        config.setPassword(ConfigFileUtil.getValue("ftp.password"));
        config.setEncoding("utf-8");
        config.setPassiveMode("false");
        config.setClientTimeout(30 * 1000);

        FTPClientFactory factory = new FTPClientFactory(config);
        FTPClientPool pool = new FTPClientPool(factory);
        ftpClient = pool.borrowClient();
        uploadFile(new File("d:/hello.txt"), "/test/");
    }

    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            log.info(localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (success == true) {
                log.info(localFile.getName() + "上传成功");
                return success;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error(localFile + "未找到");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
}

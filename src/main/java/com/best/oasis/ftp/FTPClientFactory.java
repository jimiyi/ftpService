package com.best.oasis.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ftpClient连接工厂类
 *
 * @author yiwei
 * @create 2017/8/21
 */
public class FTPClientFactory {
    // 本地字符编码
    private static String LOCAL_CHARSET = "UTF-8";

    private static Logger logger = LoggerFactory.getLogger(FTPClientFactory.class);

    private FTPClientConfig config;

    public FTPClientFactory(FTPClientConfig config) {
        this.config = config;
    }

    /**
     * 创建连接
     * @return
     * @throws Exception
     */
    public FTPClient makeClient() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(config.getClientTimeout());
        try{
            ftpClient.connect(config.getHost(),config.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.warn("FTPServer refused connection");
                return null;
            }
            boolean result = ftpClient.login(config.getUsername(), config.getPassword());
            if (!result) {
                logger.warn("ftpClient login failed... username is {}", config.getUsername());
            }
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                    "OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }
            ftpClient.setControlEncoding(LOCAL_CHARSET);
            ftpClient.setFileType(config.getTransferFileType());
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding(config.getEncoding());
            if (config.getPassiveMode().equals("true")) {
                ftpClient.enterLocalPassiveMode();
            }
        }catch (Exception e) {
            logger.error("create ftp connection failed...{}", e);
            throw e;
        }
        return ftpClient;
    }

    /**
     * 销毁连接
     * @param ftpClient
     * @throws Exception
     */
    public void destroyClient(FTPClient ftpClient) throws Exception {
        try {
            if(ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (Exception e) {
            logger.error("ftp client logout failed...{}", e);
            throw e;
        } finally {
            if(ftpClient != null) {
                ftpClient.disconnect();
            }
        }

    }

    /**
     * 验证连接
     * @param ftpClient
     * @return
     */
    public boolean validateClient(FTPClient ftpClient) {
        try {
            return ftpClient.sendNoOp();
        } catch (Exception e) {
            logger.error("Failed to validate client: {}", e);
        }
        return false;
    }
}

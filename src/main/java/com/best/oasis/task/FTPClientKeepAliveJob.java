package com.best.oasis.task;

import com.best.oasis.ftp.FTPClientPool;
import com.best.oasis.util.ConfigFileUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 保持FTP client长连接
 *
 * @author yiwei
 * @create 2017/8/25
 */

@Component("ftpClientKeepAliveJob")
public class FTPClientKeepAliveJob {
    public static final Logger log = LoggerFactory.getLogger("ftpClientKeepAliveJob");

    @Autowired
    FTPClientPool ftpClientPool;

    public void run() throws Exception{
        log.debug("向ftp服务器器发送心跳...");
        BlockingQueue<FTPClient> pool = ftpClientPool.getPool();
        if(pool.size() == Integer.parseInt(ConfigFileUtil.getValue("ftp.pool.size"))){
            Iterator<FTPClient> it = pool.iterator();
            while (it.hasNext()){
                FTPClient ftpClient = it.next();
                boolean alive = ftpClient.sendNoOp();
                if(!alive){
                    //去掉不用的client
                    ftpClientPool.invalidateClient(ftpClient);
                    //销毁连接
                    ftpClientPool.getFactory().destroyClient(ftpClient);
                    //新增client加入到连接池
                    ftpClientPool.getPool().offer(ftpClientPool.getFactory().makeClient(), 3, TimeUnit.SECONDS);
                }
            }
        }
        log.info("FTP连接池现有连接数：" + pool.size());
    }
}

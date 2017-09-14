package com.best.oasis.ftp;

import com.best.oasis.util.ConfigFileUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ftpClient连接池
 *
 * @author yiwei
 * @create 2017/8/21
 */
public class FTPClientPool {
    private static Logger logger = LoggerFactory.getLogger(FTPClient.class);
    private static final int DEFAULT_POOL_SIZE = Integer.parseInt(ConfigFileUtil.getValue("ftp.pool.size"));

    private BlockingQueue<FTPClient> pool;

    private FTPClientFactory factory;

    public FTPClientPool(FTPClientFactory factory) throws Exception {
        this(DEFAULT_POOL_SIZE, factory);
    }

    public FTPClientPool(int poolSize, FTPClientFactory factory) throws Exception {
        this.factory = factory;
        this.pool = new ArrayBlockingQueue<FTPClient>(poolSize);
        initPool(poolSize);
    }

    /**
     * 初始化连接池
     *
     * @param maxPoolSize 最大连接数
     * @throws Exception
     */
    private void initPool(int maxPoolSize) throws Exception {
        int count = 0;
        while (count < maxPoolSize) {
            pool.offer(factory.makeClient(), 3, TimeUnit.SECONDS);
            count++;
        }
    }

    /**
     * 获取ftpClient对象
     *
     * @return
     * @throws Exception
     * @throws NoSuchElementException
     * @throws IllegalStateException
     */
    public FTPClient borrowClient() throws Exception {
        FTPClient client = pool.take();
        if(client == null) {
            client = factory.makeClient();
            addClient(client);
        } else if(!factory.validateClient(client)) {
            invalidateClient(client);
            client = factory.makeClient();
            addClient(client);
        }

        return client;
    }

    /**
     * 添加客户端
     * @param ftpClient
     */
    private void addClient(FTPClient ftpClient){
        pool.offer(ftpClient);
    }

    /**
     * 归还一个对象，如果3秒内无法插入对象池，则销毁这个对象
     * @param ftpClient
     * @throws Exception
     */
    public void returnClient(FTPClient ftpClient) throws Exception {
        if ((ftpClient != null) && !pool.offer(ftpClient,3,TimeUnit.SECONDS)) {
            try {
                factory.destroyClient(ftpClient);
            } catch (IOException e) {
                logger.error("Return ftp client error",e);
            }
        }
    }

    /**
     * 使客户端无效
     * @param ftpClient
     * @throws Exception
     */
    public void invalidateClient(FTPClient ftpClient) throws Exception {
        pool.remove(ftpClient);
    }

    public void close() throws Exception {
        while(pool.iterator().hasNext()){
            FTPClient client = pool.take();
            factory.destroyClient(client);
        }
    }

    public BlockingQueue<FTPClient> getPool() {
        return pool;
    }

    public FTPClientFactory getFactory() {
        return factory;
    }
}

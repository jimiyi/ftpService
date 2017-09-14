package com.best.oasis.test;

import com.best.oasis.service.FTPService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FTPService测试类
 *
 * @author yiwei
 * @create 2017/8/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-config.xml"})
public class FTPServiceTest {
    @Autowired
    FTPService ftpService;

    @Test
    public void upload() throws Exception{
        File file = new File("d:\\hello.txt");
        FileInputStream in = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("hello.txt","hello.txt","txt",in);
        MockMultipartFile[] mockMultipartFiles = {mockMultipartFile};
        ftpService.upload("hello.txt",mockMultipartFiles,"test");
    }

    @Test
    public void download() throws Exception{

    }

    @Test
    public void append() throws Exception {
        InputStream in = new ByteArrayInputStream("\r\n你好全世界".getBytes());
        ftpService.append("/yiwei/测试.txt",in);
    }
}

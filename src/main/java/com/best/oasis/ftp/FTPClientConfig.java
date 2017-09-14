package com.best.oasis.ftp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ftpClient配置类
 *
 * @author yiwei
 * @create 2017/8/21
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class FTPClientConfig {
    private String host;

    private int port;

    private String username;

    private String password;

    private String passiveMode;

    private String encoding;

    private int clientTimeout;

    private int bufferSize;

    private int transferFileType;

    private boolean renameUploaded;

    private int retryTime;
}

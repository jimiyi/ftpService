package com.best.oasis.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by yiwei on 2017/3/11.
 */
public final class ConfigFileUtil {
    private static String CONFIG_FILE = "application.properties";
    public static String getValue(String key){
        Resource res = new ClassPathResource(CONFIG_FILE);
        String value = "";
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(res);
            value = props.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}

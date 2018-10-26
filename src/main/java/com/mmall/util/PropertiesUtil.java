package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    static {
        String fileName = "mmall.properties";
        properties = new Properties();
        try {
            logger.info("{}", PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName));
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            logger.error("配置文件读取错误{}", e);
        }
    }

    public static String getValue(String key){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value;
    }


    public static String getValue(String key, String defaultStr){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return defaultStr;
        }
        return value;
    }
}

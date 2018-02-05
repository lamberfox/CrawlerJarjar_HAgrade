package com.common.utility;

import com.common.redis.RedisUtil;

import java.io.InputStream;
import java.util.Properties;

/**
 * @authe tonyjarjar
 * @create 2017/9/8
 */
public class CommonUtils {

    /**
     * 读取文件
     * @param path
     * @return
     */
    public static  Properties loadFile(String path){
        Properties properties = new Properties();
        try {
            //当前类
            InputStream inputStream = RedisUtil.class.getResourceAsStream(path);
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  properties;
    }

}

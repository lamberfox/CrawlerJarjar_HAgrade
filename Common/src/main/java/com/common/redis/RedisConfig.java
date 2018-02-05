package com.common.redis;

/**
 * Created by tonyjarjar on 2017/7/27.
 */
public class RedisConfig {

    public  String ADDR = "0.0.0.0";


    public  int PORT = 6379;

    public  String AUTH = "CrawlerJarjar";

    public  int MAX_ACTIVE = 2048;

    public  int MAX_IDLE = 1024;

    public  int MAX_WAIT = 10000;

    public  int TIMEOUT = 50000;

    public  boolean TEST_ON_BORROW = true;
}

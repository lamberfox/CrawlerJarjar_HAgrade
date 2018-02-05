package com.common.redis;

import com.common.utility.CommonUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Properties;

/**
 * @auther tonyjarjar
 * @create2017/9/20
 */
public class RedisUtil {
    /**
     * ��ʼ��redis���ӳ�
     *
     * @param path
     * @return
     */

    public static JedisPool initRedisPool(String path) {

        Properties properties = CommonUtils.loadFile(path);

        RedisConfig redisConfig = new RedisConfig();
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        int port = 0;
        int timeout = 0;
        int db = 0;
        String auth = null;
        String addr = null;

        try {
            poolConfig.setMaxTotal(redisConfig.MAX_ACTIVE);
            poolConfig.setMaxIdle(redisConfig.MAX_IDLE);
            poolConfig.setMaxWaitMillis(redisConfig.MAX_WAIT);
            poolConfig.setTestOnBorrow(redisConfig.TEST_ON_BORROW);
            poolConfig.setBlockWhenExhausted(true);
            poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
            poolConfig.setJmxEnabled(true);
            poolConfig.setJmxNamePrefix("pool");
            poolConfig.setLifo(true);
            poolConfig.setNumTestsPerEvictionRun(20);
            poolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setTimeBetweenEvictionRunsMillis(3600000);

            port = Integer.parseInt(properties.getProperty("port"));
            timeout = Integer.parseInt(properties.getProperty("timeout"));
            db = Integer.parseInt(properties.getProperty("db"));
            auth = properties.getProperty("auth");
            addr = properties.getProperty("addr");

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        JedisPool jedisPool = new JedisPool(poolConfig, addr, port, timeout, auth, db);

        return jedisPool;
    }

}

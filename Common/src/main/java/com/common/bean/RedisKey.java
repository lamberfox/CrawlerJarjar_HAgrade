package com.common.bean;

/**
 * author:24KTai
 * time:2017-08-23 16:31
 * describe: 获取redis 的key
 */
public interface RedisKey {
    /**
     * 获取redis缓存的key
     *
     * @return
     */
     String getRedisKey();
}

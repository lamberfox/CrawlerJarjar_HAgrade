package com.common.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by tonyjarjar on 2017/7/27.
 */

public class RedisPool {

    private  JedisPool jedisPool;

    public RedisPool(String path) {
        this.jedisPool = RedisUtil.initRedisPool(path);
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized  Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static void returnResource(final Jedis jedis) {
        //方法参数被声明为final，表示它是只读的。
        if (jedis != null) {
            //jedisPool.returnResource(jedis);
            //jedis.close()取代jedisPool.returnResource(jedis)方法将3.0版本开始
            jedis.close();
        }
    }



}

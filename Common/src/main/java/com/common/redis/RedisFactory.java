package com.common.redis;

import com.common.utility.ComLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @auther tonyjarjar
 * @create 2017/9/8
 */
public class RedisFactory {

    //redis db0
    public static JedisPool redis0Pool = RedisUtil.initRedisPool("/redis0pool.properties");

    //redis db1
    public static JedisPool redis1Pool =  RedisUtil.initRedisPool("/redis1pool.properties");


    //获取redis实例
    public synchronized static  Jedis getJedis(JedisPool jedisPool) {
        try {
            if (jedisPool != null) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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



    public static void main(String[] args) {
        try {
            redis0Pool.getResource();
            redis1Pool.getResource();
            ComLogger.info("成功");
        } catch (Exception e) {
            e.printStackTrace();
            ComLogger.error("失败");
        }

    }
}

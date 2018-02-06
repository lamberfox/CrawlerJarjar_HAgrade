package com.common.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * author:tonyjarjar
 * time:2017-09-07 17:54
 * describe:redis工具类
 */
public class CacheUtil {
    /**
     * 缓存一批数据
     *
     * @param keys
     * @param values
     */
    public static boolean sets(List<String> keys, List<String> values , JedisPool jedisPool) {

        Jedis jedis = null;
        try {
            jedis = RedisFactory.getJedis(jedisPool);
            Pipeline pipeline = jedis.pipelined();
            for (int index = 0; index < keys.size(); ++index) {
                pipeline.set(keys.get(index), values.get(index));
            }
            pipeline.sync();
            pipeline.close();
        } catch (Exception e) {
            //释放redis对象
            e.printStackTrace();
            return false;
        } finally {
            //返还到连接池
            RedisPool.returnResource(jedis);
        }
        return true;
    }

    public static boolean lpush(String key, List<String> values , JedisPool jedisPool){
        Jedis jedis = null;
        try {
            jedis = RedisFactory.getJedis(jedisPool);
            Pipeline pipeline = jedis.pipelined();
            for (int index = 0; index < values.size(); ++index) {
                pipeline.lpush(key, values.get(index));
            }
            pipeline.sync();
            pipeline.close();
        } catch (Exception e) {
            //释放redis对象
            e.printStackTrace();
            return false;
        } finally {
            //返还到连接池
            RedisPool.returnResource(jedis);
        }
        return true;
    }

    public static List<String> gets(List<String> keys , JedisPool jedisPool) {
        Jedis jedis = null;
        List<String> values = null;
        try {
            jedis = RedisFactory.getJedis(jedisPool);
            Pipeline pipeline = jedis.pipelined();
            ArrayList<Response<String>> results = new ArrayList<Response<String>>();
            for (String key:keys){
                results.add(pipeline.get(key));
            }
            pipeline.sync();
            values = new ArrayList<String>();
            for (Response<String> response:results){
                values.add(response.get());
            }
        } catch (Exception e) {
            //释放redis对象
            e.printStackTrace();
        } finally {
            //返还到连接池
            RedisPool.returnResource(jedis);
        }
        return values;
    }

    public static boolean delete(String key , JedisPool jedisPool) {
        Jedis jedis = null;
        List<String> values = null;
        boolean isSuccess = true;
        try {
            jedis = RedisFactory.getJedis(jedisPool);
            jedis.del(key);
        } catch (Exception e) {
            //释放redis对象
            e.printStackTrace();
            isSuccess = false;
        } finally {
            //返还到连接池
            RedisPool.returnResource(jedis);
        }
        return isSuccess;
    }

}

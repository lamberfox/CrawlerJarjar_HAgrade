package com.model;

import com.common.serialization.Serializer;
import com.common.utility.ComLogger;
import com.common.utility.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Schedule {
    private JedisPool destPool = null;
    public String scheduleName = "";
    public TaskScheduleThread taskScheduleThread = new TaskScheduleThread();
    final int PIPELINE_LENGTH = 10000;

    public Schedule(JedisPool jedisPool, String name) {
        destPool = jedisPool;
        scheduleName = name;
    }

    public ConcurrentHashMap<String, Timing> timingMap = new ConcurrentHashMap<String, Timing>();

    private  Jedis getJedis(JedisPool jedisPool) {
        while (true){
            try {
                if (jedisPool != null) {
                    Jedis jds = jedisPool.getResource();
                    return jds;
                } else {
                    ComLogger.info("jedisPool is null");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ComLogger.info(e.toString());
                return null;
            }

        }

    }

    public class TaskScheduleThread implements Runnable {
        public void run() {
            final long timeInterval = 30 * 1000;
            while (true) {
                Jedis jedisDest = getJedis(destPool);//destPool.getResource();
                while (jedisDest == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ComLogger.info(e.toString());
                    }
                    jedisDest = getJedis(destPool);

                }
                try{
                    update(jedisDest);
                }catch (Exception e) {
                    e.printStackTrace();
                    ComLogger.info(e.toString());
                }finally{
                    //clear
                    jedisDest.close();
                }

                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ComLogger.info(e.toString());
                }
            }
        }

        private void update(Jedis jedisDest){
            int iterIndex = 0;
            int putNum = 0;
            int unputNum = 0;
            int pipeCounter = 0;
            Pipeline pipeSet = jedisDest.pipelined();
            ComLogger.info(scheduleName + " TaskScheduleThread begin...");

            Map<String, Response<Map<String, String>>> responses = new HashMap<String, Response<Map<String, String>>>(PIPELINE_LENGTH);

            for (Entry<String, Timing> entry : timingMap.entrySet()) {
                iterIndex++;

                java.util.Date now = new java.util.Date();
                String timingKey = (String) entry.getKey();
                Timing timing = (Timing) entry.getValue();

                if (timing.dispatchTime != null &&
                        timing.dispatchTime.getTime() <= now.getTime()) {
                    //modify time
                    timing.dispatchTime = new java.util.Date(timing.dispatchTime.getTime() + timing.interval * 60 * 1000);
                    if (timing.dispatchTime.getTime() <= now.getTime()) {
                        timing.dispatchTime = new java.util.Date();
                    }
                    byte[] key = SerializeUtil.serialize(scheduleName + "TaskQueue");
                    byte[] value = SerializeUtil.serialize(timing.id);
                    pipeSet.lpush(key, value);
                    pipeSet.expire(key, 60 * 10);
                    putNum++;
                    pipeCounter++;
                } else {
                    unputNum++;
                }
                //append task
                if (pipeCounter < 10000 &&
                        iterIndex < timingMap.size()) {
                    continue;
                }
                try {
                    pipeSet.sync();
                } catch (Exception e) {
                    e.printStackTrace();
                    ComLogger.info(e.toString());
                }
                //reset
                responses.clear();
                pipeCounter = 0;
            }
            ComLogger.info(scheduleName + " TaskScheduleThread end iterIndex:" + iterIndex + ",putNum"
                    + putNum + ",unputNum:" + unputNum + ",total:" + timingMap.size());
        }


    }
}

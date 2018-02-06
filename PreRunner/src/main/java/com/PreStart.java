package com;


import java.util.ArrayList;
import java.util.List;

import com.common.redis.RedisFactory;
import com.model.*;
import com.query.QuerySource;
import redis.clients.jedis.JedisPool;

/**
 * @auther tonyjarjar
 * @create 2018/2/5
 */
public class PreStart {

    private static final long taskUpd = 20 * 1000L;

    /**
    *
    *@author: tonyjarjar
    *@Description: 开始前,先将采集的站点全部load入redis中
    *@Date: 2018/2/5
    */
    public static void main(String[] args)
    {
        new Thread(new Runnable() {
            public void run() {
                preload();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                load();
            }
        }).start();
    }

    private static void preload() {
        ArrayList<Task> failTask = new ArrayList<Task>();
        CacheManager cache = null;
        long startTime = 0;
        long endTime = 0;
        while (true) {
            if (cache == null && System.currentTimeMillis() > taskUpd + endTime) {
                cache = new CacheManager();
                boolean isStart; //初始化是否成功
                long time = System.currentTimeMillis();
                if (endTime == 0) { //第一次加载
                    isStart = cache.start();
                } else {
                    isStart = cache.start(failTask, startTime);
                    failTask.clear();
                }

                if (isStart) {
                    startTime = time; //更新开始时间
                } else {
                    cache = null;
                }
            } else if (cache != null && cache.isCacheFinish()) {
                if (!cache.isCacheSuccess()) {
                    failTask = cache.getFailTasks();
                }
                cache = null;
                endTime = System.currentTimeMillis();
                System.out.println("time:" + (endTime - startTime) + "fail:" + failTask.size());
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void load()
    {
        //redis db1
        JedisPool sourcePool = RedisFactory.redis1Pool;
        //redis db2
        JedisPool destPool = RedisFactory.redis2Pool;

        Schedule generalSchedule = new Schedule(destPool, "General");

        List<Schedule> scheduleList = new ArrayList();
        scheduleList.add(generalSchedule);

        QuerySource qs = new QuerySource(sourcePool, scheduleList);
        new Thread(qs).start();
        while (!qs.isInited) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //generalSchedule.TaskScheduleThread generalTaskSchedule= new generalSchedule.TaskScheduleThread();
        new Thread(generalSchedule.taskScheduleThread).start();

        /// ... 当关闭应用程序时:
        //sourcePool.destroy();
    }

}

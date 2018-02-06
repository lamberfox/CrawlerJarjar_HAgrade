package com.query;

import com.google.gson.Gson;
import com.model.Schedule;
import com.model.Timing;
import com.common.utility.ComLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

public class QuerySource implements Runnable {
    public boolean isInited = false;
    private final int PIPELINE_LENGTH = 10000;
    //时间间隔
    private final long Interval = 60 * 1000;
    private JedisPool sourcePool = null;
    List<Schedule> scheduleList = null;

    public QuerySource(JedisPool sourcePool, List<Schedule> list) {
        this.sourcePool = sourcePool;
        this.scheduleList = list;
    }

    public void run() {
        clearUpdate();
        for (Schedule schedule : scheduleList) {
            load(schedule);
        }
        isInited = true;
        while (true) {
            updateOnce();
            // try sleep
            try {
                Thread.sleep(Interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateOnce() {
        Jedis jedisSrc = this.sourcePool.getResource();
        Pipeline pipeGet = jedisSrc.pipelined();
        List<Response<String>> idResponses = new ArrayList<Response<String>>();
        Map<String, Response<String>> timingResponses = new HashMap<String, Response<String>>(PIPELINE_LENGTH);
        //int iterIndex = 0;

        int count = 0;
        boolean needBreak = false;
        while (true) {
            count++;
            idResponses.add(pipeGet.lpop("updateList"));
            if (count < PIPELINE_LENGTH) {
                continue;
            }
            pipeGet.sync();
            for (Response<String> resp : idResponses) {
                String timingKey = resp.get();
                if (timingKey == null) {
                    needBreak = true;
                    break;
                }
                timingResponses.put(timingKey, pipeGet.get(timingKey));
            }
            pipeGet.sync();

            for (String k : timingResponses.keySet()) {
                String timingJson = timingResponses.get(k).get();
                Gson gson = new Gson();
                Timing timing = gson.fromJson(timingJson, Timing.class);

                for (Schedule schedule : scheduleList) {
                    if (k.startsWith(schedule.scheduleName.toLowerCase())) {
                        updateScheduleTiming(schedule, timing.id, timing.interval);
                        break;
                    }

                }
            }
            //reset
            count = 0;
            idResponses.clear();
            timingResponses.clear();
            if (needBreak) {
                break;
            }

        }
        jedisSrc.close();
    }

    private void clearUpdate() {
        Jedis jedisSrc = this.sourcePool.getResource();
        Pipeline pipeGet = jedisSrc.pipelined();
        List<Response<String>> responses = new ArrayList<Response<String>>();
        int count = 0;
        boolean needBreak = false;

        while (true) {
            count++;
            responses.add(pipeGet.lpop("updateList"));
            if (count < PIPELINE_LENGTH) {
                continue;
            }
            pipeGet.sync();

            for (Response<String> resp : responses) {
                String value = resp.get();
                if (value == null ||
                        value == "") {
                    needBreak = true;
                    break;
                }
            }
            //reset
            count = 0;
            responses.clear();
            if (needBreak) {
                break;
            }

        }
        jedisSrc.close();
    }

    private void load(Schedule schedule) {
        String keys = schedule.scheduleName.toLowerCase() + "_timing_*";

        Jedis jedisSrc = this.sourcePool.getResource();
        Pipeline pipeGet = jedisSrc.pipelined();
        Set<String> keysSet = jedisSrc.keys(keys);

        Map<String, Response<String>> responsesMap = new HashMap<String, Response<String>>(PIPELINE_LENGTH);
        int count = 0;
        int iterIndex = 0;
        for (String key : keysSet) {
            iterIndex++;
            count++;
            responsesMap.put(key, pipeGet.get(key));

            if (count < PIPELINE_LENGTH && iterIndex < keysSet.size()) {
                continue;
            }
            pipeGet.sync();
            for (String k : responsesMap.keySet()) {
                String valueJson = responsesMap.get(k).get();
                Gson gson = new Gson();
                Timing value = gson.fromJson(valueJson, Timing.class);
                //
                String id = value.id;//String.valueOf();
                int interval = value.interval;
                updateScheduleTiming(schedule, id, interval);
            }
            count = 0;
            responsesMap.clear();
            if (iterIndex % 20000 == 0) {
                ComLogger.info(schedule.scheduleName.toLowerCase() + " iterIndex:" + iterIndex);
            }
        }
        jedisSrc.close();
    }

    private void updateScheduleTiming(Schedule schedule, String id, int interval) {
        if (schedule.timingMap.containsKey(id)) {
            Timing t = schedule.timingMap.get(id);
            t.interval = interval;
            return;
        }
        Timing t = new Timing();
        t.id = id;
        t.interval = interval;
        t.dispatchTime = new Date(new Date().getTime() - 1000);
        schedule.timingMap.put(id, t);
    }
}


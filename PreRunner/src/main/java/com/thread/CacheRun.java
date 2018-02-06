package com.thread;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.common.bean.*;
import com.common.redis.CacheUtil;
import com.common.redis.RedisFactory;
import com.model.Task;
import com.query.ExteriorQuery;
import com.query.SqlSyntax;

import java.util.*;

/**
 * author:24KTai
 * time:2017-08-10 18:19
 * describe:redis缓存线程
 */
public class CacheRun implements Runnable {

    private Task task;

    private CacheListerner cacheListerner;

    private Custom[] customs;

    public CacheRun(Task task, CacheListerner listerner) {
        this.task = task;
        cacheListerner = listerner;
    }

    public void run() {
        boolean isSuccess = true;
        String count = "";

        try {
            ArrayList<RedisKey> redisKeys = (ArrayList<RedisKey>) ExteriorQuery.query(task.getSql(),
                    task.getDataClass(), task.getRetry());
             count += redisKeys.size();
            if (redisKeys == null) {
                isSuccess = false;
            } else if (redisKeys.size() > 0) {
                if (task.getDataClass().equals(General.class)) {
                    customs = getCustoms(redisKeys);
                    if (customs != null){
                        count += "|" + customs.length;
                        isSuccess = true;
                    } else {
                        isSuccess =false;
                    }
                }
                if (isSuccess){
                    isSuccess = redisCache(redisKeys);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            task.setSuccess(isSuccess);
            String log = count + "   " + isSuccess + "   " + task.getSql();
            System.out.println(log);
            cacheListerner.result(isSuccess, task);
        }
    }

    public static void main(String[] args){
        ArrayList<Custom> keys = new ArrayList<>();
        Random random = new Random();
        for (int index = 0; index < 5; ++index){
            Custom general = new Custom();
            general.setId(random.nextInt(100));
            keys.add(general);
        }

        "".toString();
    }

    private  Custom[] getCustoms(ArrayList<RedisKey> keys) {
        String  sql = SqlSyntax.getCustomSql(keys);
        ArrayList<Custom> customs = (ArrayList<Custom>) ExteriorQuery.query(sql, Custom.class);
        Custom [] array = customs.toArray(new Custom[0]);
        Arrays.sort(array, new Comparator<Custom>() {
            @Override
            public int compare(Custom o1, Custom o2) {
                return o1.getId() - o2.getId();
            }
        });
        return array;
    }

    /**
     * 缓存数据
     *
     * @param list
     * @return
     */
    private boolean redisCache(ArrayList<RedisKey> list) {
        ArrayList<String> jsons = getNormalJson(list);
        ArrayList<String> keys = getNormalKeys(list);
        boolean isSuccess = CacheUtil.sets(keys, jsons, RedisFactory.redis0Pool);
        boolean isTiming = isCacheTiming(list.get(0));

        if (isTiming) {
            jsons = getTimingJson(list);
            keys = getTimingKeys(keys);
            isSuccess = CacheUtil.sets(keys, jsons, RedisFactory.redis1Pool)
                    && CacheUtil.lpush("updateList", keys, RedisFactory.redis1Pool);
        }
        return isSuccess;
    }


    private Custom getCustom(int id){
        Custom custom = new Custom();
        custom.setId(id);
        int index = Arrays.binarySearch(customs, custom, new Comparator<Custom>() {
            @Override
            public int compare(Custom o1, Custom o2) {
                return o1.getId() - o2.getId();
            }
        });
        if (index >= 0){
            return customs[index];
        }
        return null;
    }

    /**
     * 是否缓存timing
     *
     * @param var
     * @return
     */
    private boolean isCacheTiming(Object var) {
        if ( var instanceof General) {
            return true;
        }

        return false;
    }

    /**
     * 获取json字符串
     *
     * @param list
     * @return
     */
    private ArrayList<String> getNormalJson(ArrayList<RedisKey> list) {
        Gson gson = new Gson();
        ArrayList<String> jsons = new ArrayList<String>();

        for (int index = 0; index < list.size(); ++index) {
            RedisKey redisKey = list.get(index);
            if (redisKey instanceof General) {
                General general = (General) redisKey;
                general.setCustom(getCustom(general.getId()));
            }
            String json = gson.toJson(redisKey);
            jsons.add(json);
        }

        return jsons;
    }

    /**
     * 获取普通redis的key
     *
     * @param list
     * @return
     */
    private ArrayList<String> getNormalKeys(ArrayList<RedisKey> list) {
        ArrayList<String> keys = new ArrayList<String>();

        for (int index = 0; index < list.size(); ++index) {
            keys.add(list.get(index).getRedisKey());
        }

        return keys;
    }

    /**
     * 获取timing的key
     *
     * @param list
     * @return
     */
    private ArrayList<String> getTimingKeys(ArrayList<String> list) {
        ArrayList<String> keys = new ArrayList<String>();

        for (String var : list) {
            String[] splits = var.split("_");
            String key = splits[0] + "_timing_" + splits[1];
            keys.add(key);
        }

        return keys;
    }

    /**
     * 获取timing的json字符串
     *
     * @param list
     * @return
     */
    private ArrayList<String> getTimingJson(ArrayList<RedisKey> list) {
        Gson gson = new Gson();
        ArrayList<String> jsons = new ArrayList<String>();

        for (int index = 0; index < list.size(); ++index) {
            RedisKey redisKey = list.get(index);
            JsonObject jsonObject = gson.toJsonTree(redisKey).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> sets = jsonObject.entrySet();
            Iterator<Map.Entry<String, JsonElement>> iterator = sets.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> entry = iterator.next();
                String key = entry.getKey().toLowerCase();
                if (key.equals("id") || key.equals("interval")
                        || key.equals("addon")) {
                    continue;
                }
                jsonObject.remove(entry.getKey());
            }

            jsons.add(jsonObject.toString());
        }
        return jsons;
    }


}

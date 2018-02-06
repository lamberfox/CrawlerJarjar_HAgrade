package com.model;


/**
 * author:tonyjarjar
 * time:2017-08-10 17:38
 * describe: redis缓存任务
 */
public class Task {
    //重试几次
    public final static int DEFUALT_RETRY = 3;

    //数据默认加载多少条
    public final static int NUMBER = 5000;

    //数据对应实体类
    private Class dataClass;

    //任务重试几次
    private int retry;

    //任务是否成功完成
    private boolean isSuccess;

    //sql查询语句
    private String  sql;

    public Task(Class dataClass, String sql) {
        this.dataClass = dataClass;
        this.sql = sql;
        retry = DEFUALT_RETRY;
    }


    public Class getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class dataClass) {
        this.dataClass = dataClass;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}

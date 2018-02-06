package com.thread;


import com.model.Task;

/**
 * author:24KTai
 * time:2017-08-10 18:25
 * describe: 运行结果监听
 */
public interface CacheListerner {
    /**
     * 任务运行结果
     * @param isSuccess 执行是否成功
     * @param task 任务属性
     */
    void result(boolean isSuccess, Task task);
}

package com;

import com.common.bean.*;
import com.common.redis.CacheUtil;
import com.common.redis.RedisFactory;
import com.model.TableInfo;
import com.model.TableWeight;
import com.model.Task;
import com.model.Urgent;
import com.query.*;
import com.thread.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author:24KTai
 * time:2017-09-05 15:44
 * describe:
 */
public class CacheManager {
    private ArrayList<Task> tasks;

    //最大线程数
    private int maxThreadCount = 5;

    //线程池
    private ExecutorService fixedThreadPool;

    //执行失败任务
    private ArrayList<Task> failTasks;

    //执行成功任务
    private ArrayList<Task> successTasks;

    //数据库表名
    private String[] tables;

    //线程对象锁
    private Object lockObject;

    public CacheManager() {
        tables = new String[]{"general", "weixin", "weibo", "`group`"};
        fixedThreadPool = Executors.newFixedThreadPool(maxThreadCount);
        tasks = new ArrayList<>();
        lockObject = new Object();
        failTasks = new ArrayList<>();
        successTasks = new ArrayList<>();
    }

    /**
     * 加载所有数据
     *
     * @return
     */
    public boolean start() {
        return start(true, null, 0);
    }

    /**
     * 增量更新数据
     *
     * @param lastFailTask
     * @param time
     * @return
     */
    public boolean start(ArrayList<Task> lastFailTask, long time) {
        return start(false, lastFailTask, time);
    }

    /**
     * 开始启动，
     *
     * @param isLoadAll    是否加载全部任务
     * @param lastFailTask 上次更新任务
     * @param time         增量更新时间
     * @return
     */
    private boolean start(boolean isLoadAll, ArrayList<Task> lastFailTask, long time) {
        try {
            //TODO 添加紧急任务，暂时先注释掉,后面根据实际任务紧急表，修改参数
//            tasks.addAll(getUrgentTasks("`group`"));
            if (isLoadAll) {
                if (!CacheUtil.delete("updateList", RedisFactory.redis0Pool)) {
                    return false;
                }
                tasks.addAll(getWeightTasks());
                tasks.addAll(getLoadTasks());
            } else {
                //加载增量更新任务
                tasks.addAll(getUpdateTasks(tables, time));
            } //

            if (lastFailTask != null && lastFailTask.size() > 0) {
                tasks.addAll(lastFailTask);
            }

            for (Task task : tasks) {
                fixedThreadPool.execute(new CacheRun(task, cacheListerner));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 缓存是否成功
     *
     * @return
     */
    public boolean isCacheSuccess() {
        if (tasks.size() == successTasks.size()) {
            return true;
        }
        return false;
    }

    /**
     * 缓存是否结束
     *
     * @return
     */
    public boolean isCacheFinish() {
        if (tasks.size() == 0) {
            return false;
        }
        if (tasks.size() == successTasks.size() + failTasks.size()) {
            return true;
        }
        return false;
    }

    public ArrayList<Task> getFailTasks() {
        return failTasks;
    }


    /**
     * 获取紧急任务
     *
     * @return
     */
    private ArrayList<Task> getUrgentTasks(String table) {
        //TODO
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Urgent> urgents = (ArrayList<Urgent>) ExteriorQuery.query(SqlSyntax.getIdSql(table), Urgent.class);
        Task task = new Task(General.class, SqlSyntax.getUrgentSql("general", urgents));
        tasks.add(task);
        /*task = new Task(Weixin.class, SqlSyntax.getUrgentSql("weixin", urgents));
        tasks.add(task);
        task = new Task(Weibo.class, SqlSyntax.getUrgentSql("weibo", urgents));
        tasks.add(task);*/
        return tasks;
    }


    /**
     * 获取权重任务
     *
     * @return
     */
    private ArrayList<Task> getWeightTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        ArrayList<TableWeight> weights = getTableWeights();
        ArrayList<String> sqls = SqlSyntax.getLoadWeightSqls(weights);
        for (int index = 0; index < sqls.size(); ++index) {
            String table = weights.get(index).getTable();
            Task task = new Task(getClass(table), sqls.get(index));
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * 获取加载任务
     *
     * @return
     */
    private ArrayList<Task> getLoadTasks() {
        ArrayList<TableInfo> tableInfos = getTableInfos(tables);
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (TableInfo tableInfo : tableInfos) {
            ArrayList<String> sqls = SqlSyntax.getLoadSqlArray(tableInfo, Task.NUMBER);
            String table = tableInfo.getTable();
            for (int index = 0; index < sqls.size(); ++index) {
                Task task = new Task(getClass(table), sqls.get(index));
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * 获取更新任务
     *
     * @param tables
     * @param time
     * @return
     */
    private ArrayList<Task> getUpdateTasks(String[] tables, long time) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int index = 0; index < tables.length; ++index) {
            String sql = SqlSyntax.getUpdateSql(tables[index], time);
            Task task = new Task(getClass(tables[index]), sql);
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * 获取数据库表信息
     *
     * @param tables
     * @return
     */
    private ArrayList<TableInfo> getTableInfos(String[] tables) {
        ArrayList<TableInfo> querys = new ArrayList<TableInfo>();
        for (int index = 0; index < tables.length; ++index) {
            String sql = SqlSyntax.getTableInfoSql(tables[index]);
            ArrayList<TableInfo> tableInfos = (ArrayList<TableInfo>)  ExteriorQuery.query(sql, TableInfo.class);
            if (tableInfos == null) {
                return null;
            }
            tableInfos.get(0).setTable(tables[index]);
            querys.addAll(tableInfos);
        }
        return querys;
    }

    /**
     * 获取表的权重
     *
     * @return
     */
    private ArrayList<TableWeight> getTableWeights() {
        String[] tables = {"general", "weixin", "weibo"};
        ArrayList<TableWeight> querys = new ArrayList<TableWeight>();
        for (int index = 0; index < tables.length; ++index) {
            String sql = SqlSyntax.getWeightLevelSql(tables[index]);
            ArrayList<TableWeight> temp = (ArrayList<TableWeight>)  ExteriorQuery.query(sql, TableWeight.class);
            if (temp == null) {
                return null;
            }
            for (TableWeight weight : temp) {
                weight.setTable(tables[index]);
            }
            querys.addAll(temp);
        }
        querys = sortTableWeights(querys);
        ArrayList<TableWeight> results = new ArrayList<TableWeight>();
        for (TableWeight tableWeight : querys) {
            if (tableWeight.getWeight() != 0) {
                results.add(tableWeight);
            }
        }
        return results;
    }


    /**
     * 获取表的实体
     *
     * @param table
     * @return
     */
    private Class getClass(String table) {
        table = table.toLowerCase();
        if (table.equals("general")) {
            return General.class;
        }
        return null;
    }

    /**
     * 按照权重排序，权重越大越靠前
     * @param tableWeights
     * @return
     */
    private ArrayList<TableWeight> sortTableWeights(ArrayList<TableWeight> tableWeights) {
        Collections.sort(tableWeights, new Comparator<TableWeight>() {
            public int compare(TableWeight o1, TableWeight o2) {
                return o2.getWeight() - o1.getWeight();
            }
        });
        return tableWeights;
    }




    /**
     * 监听程序运行
     */
    private CacheListerner cacheListerner = new CacheListerner() {
        public void result(boolean isSucess, Task task) {
            synchronized (lockObject) {
                if (isSucess) {
                    successTasks.add(task);
                } else {
                    failTasks.add(task);
                }
            }
        }
    };

}

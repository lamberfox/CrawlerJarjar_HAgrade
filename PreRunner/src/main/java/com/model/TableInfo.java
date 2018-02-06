package com.model;

/**
 * author:24KTai
 * time:2017-09-05 16:40
 * describe: 表里面的最小id和最大id信息
 */
public class TableInfo {
    @NoUse
    private String table;

    //数据库表的最小id
    private int minID;

    //数据库表最大id
    private int maxID;



    public int getMinID() {
        return minID;
    }

    public void setMinID(int minID) {
        this.minID = minID;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}

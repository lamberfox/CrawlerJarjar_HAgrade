package com.model;

/**
 * author:24KTai
 * time:2017-08-21 11:01
 * describe: 表信息
 */
public class TableWeight  {
    //表名
    @NoUse
    private String Table;

    //权重
    protected int Weight;


    public TableWeight(String table) {
        Table = table;
    }

    public TableWeight() {
    }

    public String getTable() {
        return Table;
    }

    public void setTable(String table) {
        Table = table;
    }


    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        Weight = weight;
    }

}

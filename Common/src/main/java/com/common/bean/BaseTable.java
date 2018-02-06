package com.common.bean;

/**
 * author:24KTai
 * time:2017-08-21 11:18
 * describe:表基类
 */
public abstract class BaseTable implements RedisKey{
    protected int id;

    protected String name;

    //入库时间
    protected String addOn;

    //更新时间
    private String updateOn;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddOn() {
        return addOn;
    }

    public void setAddOn(String addOn) {
        this.addOn = addOn;
    }

    public String getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(String updateOn) {
        this.updateOn = updateOn;
    }
}

package com.common.bean;

/**
 * author:24KTai
 * time:2017-08-14 11:00
 * describe:custom表
 */
public class Custom extends BaseTable{
    private String template;

    private String code;

    private String config;

    private String hash;


    public String getRedisKey() {
        return "custom_" + id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

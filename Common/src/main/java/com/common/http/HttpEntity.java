package com.common.http;

import com.sun.jndi.toolkit.url.Uri;

import java.util.Map;

/**
 * Created by tonyjarjar on 2017/9/7.
 */
public class HttpEntity {

    public Uri uri;

    public String referer;

    public int timeout;

    public boolean expired;

    public Uri responseUri;

    /// <summary>
    /// 资源内容的类型
    /// </summary>
    public String contentType;

    /// <summary>
    /// 浏览器请求头
    /// </summary>
    public String userAgent;

    /// <summary>
    /// 浏览器伪装X-Forwarded-For
    /// </summary>
    public String forWarded;

    /// <summary>
    /// 是否只下载文本类型的流
    /// </summary>
    public boolean textLimited;

    /// <summary>
    ///
    /// </summary>
    public byte[] data;

    /// <summary>
    ///
    /// </summary>
    public Map<String,String> paramter;

    /// <summary>
    ///
    /// </summary>
    public String source;

    public HttpEntity(Uri uri)
    {
        this.uri = uri;
    }

    public HttpEntity(Uri uri,Map<String,String> map)
    {
        this.uri = uri;
        this.paramter = map;
    }
}

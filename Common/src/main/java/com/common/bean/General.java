package com.common.bean;

/**
 * author:24KTai
 * time:2017-08-10 10:50
 * describe:
 */
public class General extends BaseTable {
    //信源类型
    private int category;

    //频道/栏目
    private int channel;

    //1.站点首页 2.频道首页 3.栏目首页
    private int homePage;

    //大洲
    private int continent;

    //国家
    private int country;

    //省市
    private int province;

    //城市
    private int city;

    //语言
    private int language;

    //是否是海外 1-true 0-false
    private int overseas;

    //计划任务
    private int scheduleID;

    //采集间隔(分钟)
    private int interval;

    //采集深度
    private int depth;

    //采集范围(1.正文 2.回帖 4.更新回帖)
    private int coverage;

    //子线程数
    private int threads;

    //网页编码
    private int encoding;

    //是否使用Cookie 1-true 0-false
    private int cookie;

    //下载超时的次数上限
    private int timeoutLimit;

    //网页渲染方式(1.IE 2.PhantomJS)
    private int rendering;

    //下载间隔延时(秒)
    private int delay;

    //网页重定向方式(1.meta 2.frame 4.iframe)
    private int redirection;

    //源代码处理方式(1.html decode 2.清理注释 3.清理js 4.清理css)
    private int depuration;

    //判重用的历史链接数
    private int historyLinks;

    //等待解析链接的超时时间(秒)
    private int waitingLimit;

    //正文页的链接正则
    private String textMask;

    //链接页的链接正则
    private String linkMask;

    //内容翻页的链接正则
    private String pageMask;

    //列表翻页的链接正则
    private String listMask;

    //评论翻页的链接正则
    private String replyMask;

    //阻止采集的链接规则
    private String blockMask;

    //自动解析翻页的方式
    private int pageMode;

    //标题解析级别(0.title 1.title->node 2.meta 3.font 4.style 5.link 6.script 7.iframe)
    private int titleLevel;

    //标题杂质
    private String titleNoise;

    //图片处理方式(1.原图 2.缩略图)
    private int image;

    //处理器(处理器的类名)
    private String processor;

    //定制参数
    private int customID;

    //组ID
    private int groupID;

    //站点id
    private int siteID;

    //标签id
    private int labelID;

    //采集数据的发送目标
    private int destinationID;

    //信源权重接口
    private int weight;

    private String comment;

    //是否暂停 1-true 0-false
    private int pause;

    //站点链接
    private String url;

    private Custom custom;

    public String getRedisKey() {
        return "general_" + id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getHomePage() {
        return homePage;
    }

    public void setHomePage(int homePage) {
        this.homePage = homePage;
    }

    public int getContinent() {
        return continent;
    }

    public void setContinent(int continent) {
        this.continent = continent;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getOverseas() {
        return overseas;
    }

    public void setOverseas(int overseas) {
        this.overseas = overseas;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getCoverage() {
        return coverage;
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public int getCookie() {
        return cookie;
    }

    public void setCookie(int cookie) {
        this.cookie = cookie;
    }

    public int getTimeoutLimit() {
        return timeoutLimit;
    }

    public void setTimeoutLimit(int timeoutLimit) {
        this.timeoutLimit = timeoutLimit;
    }

    public int getRendering() {
        return rendering;
    }

    public void setRendering(int rendering) {
        this.rendering = rendering;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getRedirection() {
        return redirection;
    }

    public void setRedirection(int redirection) {
        this.redirection = redirection;
    }

    public int getDepuration() {
        return depuration;
    }

    public void setDepuration(int depuration) {
        this.depuration = depuration;
    }

    public int getHistoryLinks() {
        return historyLinks;
    }

    public void setHistoryLinks(int historyLinks) {
        this.historyLinks = historyLinks;
    }

    public int getWaitingLimit() {
        return waitingLimit;
    }

    public void setWaitingLimit(int waitingLimit) {
        this.waitingLimit = waitingLimit;
    }

    public String getTextMask() {
        return textMask;
    }

    public void setTextMask(String textMask) {
        this.textMask = textMask;
    }

    public String getLinkMask() {
        return linkMask;
    }

    public void setLinkMask(String linkMask) {
        this.linkMask = linkMask;
    }

    public String getPageMask() {
        return pageMask;
    }

    public void setPageMask(String pageMask) {
        this.pageMask = pageMask;
    }

    public String getListMask() {
        return listMask;
    }

    public void setListMask(String listMask) {
        this.listMask = listMask;
    }

    public String getReplyMask() {
        return replyMask;
    }

    public void setReplyMask(String replyMask) {
        this.replyMask = replyMask;
    }

    public String getBlockMask() {
        return blockMask;
    }

    public void setBlockMask(String blockMask) {
        this.blockMask = blockMask;
    }

    public int getPageMode() {
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
    }

    public int getTitleLevel() {
        return titleLevel;
    }

    public void setTitleLevel(int titleLevel) {
        this.titleLevel = titleLevel;
    }

    public String getTitleNoise() {
        return titleNoise;
    }

    public void setTitleNoise(String titleNoise) {
        this.titleNoise = titleNoise;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public int getCustomID() {
        return customID;
    }

    public void setCustomID(int customID) {
        this.customID = customID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getLabelID() {
        return labelID;
    }

    public void setLabelID(int labelID) {
        this.labelID = labelID;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(int destinationID) {
        this.destinationID = destinationID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }
}

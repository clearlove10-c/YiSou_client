package com.smlz.yisounews.entity;

import java.io.Serializable;

public class NewsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer newsId;

    private String newsUrl;

    private String newsTitle;

    private String newsType;

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }
    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "newsId=" + newsId +
                ", newsUrl=" + newsUrl +
                ", newsTitle=" + newsTitle +
                ", newsType=" + newsType +
                "}";
    }

    public NewsInfo(Integer newsId, String newsUrl, String newsTitle, String newsType) {
        this.newsId = newsId;
        this.newsUrl = newsUrl;
        this.newsTitle = newsTitle;
        this.newsType = newsType;
    }
}

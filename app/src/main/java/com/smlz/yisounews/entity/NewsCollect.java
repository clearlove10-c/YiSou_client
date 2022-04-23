package com.smlz.yisounews.entity;

import java.io.Serializable;

public class NewsCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer collectId;

    private Integer userId;

    private Integer newsId;

    public Integer getCollectId() {
        return collectId;
    }

    public void setCollectId(Integer collectId) {
        this.collectId = collectId;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    @Override
    public String toString() {
        return "NewsCollect{" +
                "collectId=" + collectId +
                ", userId=" + userId +
                ", newsId=" + newsId +
                "}";
    }

    public NewsCollect(Integer collectId, Integer userId, Integer newsId) {
        this.collectId = collectId;
        this.userId = userId;
        this.newsId = newsId;
    }
}

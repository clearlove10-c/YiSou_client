package com.smlz.yisounews.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeedbackInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer feedbackId;

    private Integer userId;

    private String feedbackTime;

    private String feedbackContent;

    private String feedbackStatus;

    private String feedbackRepo;

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
    }
    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }
    public String getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(String feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }
    public String getFeedbackRepo() {
        return feedbackRepo;
    }

    public void setFeedbackRepo(String feedbackRepo) {
        this.feedbackRepo = feedbackRepo;
    }

    @Override
    public String toString() {
        return "FeedbackInfo{" +
                "feedbackId=" + feedbackId +
                ", userId=" + userId +
                ", feedbackTime=" + feedbackTime +
                ", feedbackContent=" + feedbackContent +
                ", feedbackStatus=" + feedbackStatus +
                ", feedbackRepo=" + feedbackRepo +
                "}";
    }

    public FeedbackInfo(Integer feedbackId, Integer userId, String feedbackTime, String feedbackContent, String feedbackStatus, String feedbackRepo) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.feedbackTime = feedbackTime;
        this.feedbackContent = feedbackContent;
        this.feedbackStatus = feedbackStatus;
        this.feedbackRepo = feedbackRepo;
    }

    public FeedbackInfo(Integer userId, String feedbackContent) {
        this.userId = userId;
        this.feedbackContent = feedbackContent;
    }

//    public FeedbackInfo(Integer feedbackId, Integer userId, String feedbackTime, String feedbackContent, String feedbackStatus, String feedbackRepo) {
//        this.feedbackId = feedbackId;
//        this.userId = userId;
//        this.feedbackTime = LocalDate.parse(feedbackTime);
//        this.feedbackContent = feedbackContent;
//        this.feedbackStatus = feedbackStatus;
//        this.feedbackRepo = feedbackRepo;
//    }
}
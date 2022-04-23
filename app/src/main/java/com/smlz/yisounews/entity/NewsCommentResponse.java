package com.smlz.yisounews.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class NewsCommentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer commentId;

    private Integer userId;

    private String userName;

    private String newsTitle;

    private String content;

    private String commentTime;



    public NewsCommentResponse(Integer commentId, Integer userId, String userName, String newsTitle, String content, String commentTime) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.newsTitle = newsTitle;
        this.content = content;
        this.commentTime = commentTime;
    }

    public NewsCommentResponse(Integer commentId, Integer userId, String userName, String content, String commentTime) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.commentTime = commentTime;
    }

    public NewsCommentResponse(Integer commentId, Integer userId, String userName, String content, LocalDate commentTime) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.commentTime = commentTime.toString();
    }
    public NewsCommentResponse(Integer userId, String newsTitle, String content) {
        this.userId = userId;
        this.newsTitle = newsTitle;
        this.content = content;
    }

    public NewsCommentResponse() {
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    @Override
    public String toString() {
        return "NewsCommentResponse{" +
                "commentId=" + commentId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", newsTitle='" + newsTitle + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

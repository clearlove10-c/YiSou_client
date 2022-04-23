package com.smlz.yisounews.entity;

public class NewsComment {
    String comment;
    String commentatorID;
    String commentTime;
    String commentID;
    String newsID;
    public  NewsComment(String commentatorID,String comment,String commentTime,String commentID,String newsID){
        this.comment=comment;
        this.commentatorID=commentatorID;
        this.commentTime=commentTime;
        this.commentID=commentID;
        this.newsID=newsID;
    }
    public NewsComment(){
    }
    public String getComment(){
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommmentatorID() {
        return commentatorID;
    }

    public void setCommmentatorID(String commmentatorID) {
        this.commentatorID = commmentatorID;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
}

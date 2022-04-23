package com.smlz.yisounews.entity;

public class NewsIntro {
    String newsId;
    String newsUrl;
    String newsTitle;
    public  NewsIntro(){

    }
    public NewsIntro(String newsId,String newsUrl,String newsTitle){
        this.newsId=newsId;
        this.newsUrl=newsUrl;
        this.newsTitle=newsTitle;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
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
}

package com.smlz.yisounews.entity;

import com.smlz.yisounews.util.ImageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ArticleInfoBase64 {
    private Integer articleId;

    private Integer userId;

    private String articleTitle;

    private String articleCoverBase64;

    private String articleTime;

    private String articleContent;

    public ArticleInfoBase64(Integer articleId, Integer userId, String articleTitle, String articleCoverBase64, String articleTime, String articleContent) {
        this.articleId = articleId;
        this.userId = userId;
        this.articleTitle = articleTitle;
        this.articleCoverBase64 = articleCoverBase64;
        this.articleTime = articleTime;
        this.articleContent = articleContent;
    }

    public ArticleInfoBase64() {
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleCoverBase64() {
        return articleCoverBase64;
    }

    public void setArticleCoverBase64(String articleCoverBase64) {
        this.articleCoverBase64 = articleCoverBase64;
    }

    public String getArticleTime() {
        return articleTime;
    }

    public void setArticleTime(String articleTime) {
        this.articleTime = articleTime;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    @Override
    public String toString() {
        return "ArticleInfoBase64{" +
                "articleId=" + articleId +
                ", userId=" + userId +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleCoverBase64='" + articleCoverBase64 + '\'' +
                ", articleTime='" + articleTime + '\'' +
                ", articleContent='" + articleContent + '\'' +
                '}';
    }

    public static ArticleInfo toArticleInfo(ArticleInfoBase64 articleInfoBase64) {
        try {
            return new ArticleInfo(
                    articleInfoBase64.getArticleId(),
                    articleInfoBase64.getUserId(),
                    articleInfoBase64.getArticleTitle(),
                    articleInfoBase64.getArticleCoverBase64() == null ? null :
                            ImageUtil.decode(articleInfoBase64.getArticleCoverBase64()),
                    articleInfoBase64.getArticleTime() == null ? null :
                            LocalDate.parse(articleInfoBase64.getArticleTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    articleInfoBase64.getArticleContent()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArticleInfoBase64 toArticleInfoBase64(ArticleInfo articleInfo) {
        return new ArticleInfoBase64(
                articleInfo.getArticleId(),
                articleInfo.getUserId(),
                articleInfo.getArticleTitle(),
                articleInfo.getArticleCover() == null ? null :
                        ImageUtil.encode(articleInfo.getArticleCover()),
                articleInfo.getArticleTime() == null ? null :
                        articleInfo.getArticleTime().toString(),
                articleInfo.getArticleContent()
        );
    }
}
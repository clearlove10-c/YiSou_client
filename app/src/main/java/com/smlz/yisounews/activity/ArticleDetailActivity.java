package com.smlz.yisounews.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.ArticleInfo;
import com.smlz.yisounews.entity.ArticleInfoBase64;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.smlz.yisounews.util.HttpResultListener;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ArticleDetailActivity extends BaseActivity {

    private static final String TAG = "ArticleDetailActivity";

    boolean ifDeleted = false;

    private ArticleInfo article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.article_detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //启用HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.article_detail_collapsing_toolbar);
        ImageView articleImageView = (ImageView) findViewById(R.id.article_image_view);
        TextView articleContentText = (TextView) findViewById(R.id.article_content_text);
        TextView articleAuthorText = findViewById(R.id.article_author);
        TextView articleTimeText = findViewById(R.id.article_time);

        // 此处去查询数据库
        String serverFinalUrl = MainActivity.serverUrl + "/article/getarticledetail";
        Map<String, String> mapArticle = new HashMap<>();
        mapArticle.put("articleId", String.valueOf(ArticleActivity.curArticle.getArticleId()));
        HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapArticle, new HttpResultListener() {
            @Override
            protected void onResult(HttpResult httpResult) {
                if (httpResult != null && httpResult.getCode() == 200) {
                    Log.d(TAG, httpResult.getData().toString());
                    ArticleInfoBase64 articleInfoBase64 = new Gson().fromJson((String) httpResult.getData(), ArticleInfoBase64.class);
                    article = ArticleInfoBase64.toArticleInfo(articleInfoBase64);
                }
                ArticleDetailActivity.this.runOnUiThread(() -> {
                    //设置当前界面的标题，作者，内容
                    //TODO 此处为ID应改为名字
                    collapsingToolbar.setTitle(article.getArticleTitle());
                    // 设置时间
                    articleTimeText.setText(article.getArticleTime().toString());
                    articleAuthorText.setText(article.getUserId().toString());
                    articleContentText.setText(article.getArticleContent());

                    Glide.with(ArticleDetailActivity.this)
                            .load(article.getArticleCover())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(articleImageView);
                });
            }
        });

        //监听删除点击事件
        FloatingActionButton delFab = (FloatingActionButton) findViewById(R.id.delete_article);
        delFab.setOnClickListener(view -> {
            // 点击删除文章
            new MaterialDialog.Builder(ArticleDetailActivity.this)
                    .title("提示")
                    .content("确认是否删除此篇文章")
                    .onPositive((dialog, which) -> {
                        String serverFinalUrl1 = MainActivity.serverUrl + "/article/deletearticle";
                        Map<String, String> mapArticle1 = new HashMap<>();
                        mapArticle1.put("articleId", article.getArticleId().toString());
                        mapArticle1.put("userId", MainActivity.curUser.getUserId().toString());
                        Call callArticle1 = HttpClient.getClient().doPostFormRequest(serverFinalUrl1, mapArticle1, new HttpResultListener() {
                            @Override
                            protected void onResult(HttpResult httpResult) {
                                if (httpResult != null && httpResult.getCode() == 200) {
                                    ifDeleted=true;
                                } else {
                                    ifDeleted = false;
                                    Log.d(TAG, httpResult.getMsg());
                                }
                                ArticleDetailActivity.this.runOnUiThread(() -> {
                                    if (ifDeleted) {
                                        Toast.makeText(ArticleDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                        ArticleDetailActivity.this.finish();
                                    } else {
                                        Toast.makeText(ArticleDetailActivity.this, "删除失败！不能删除他人文章", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    })
                    .positiveText("确认")
                    .negativeText("取消")
                    .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ArticleDetailActivity.this.finish();
                return true;
        }
        return true;
    }
}

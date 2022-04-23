package com.smlz.yisounews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smlz.yisounews.R;
import com.smlz.yisounews.adapter.ArticleAdapter;
import com.smlz.yisounews.entity.ArticleInfo;
import com.smlz.yisounews.entity.ArticleInfoBase64;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smlz.yisounews.util.HttpResultListener;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends BaseActivity {

    public static ArticleInfo curArticle;
    private static final String TAG = "ArticleActivity";

    private List<ArticleInfo> articleList = new ArrayList<>();
    private ArticleAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = findViewById(R.id.article_toolbar);
        toolbar.setTitle("文章列表");
        setSupportActionBar(toolbar);
        //获取到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //将导航按钮显示出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置一个导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        //监听编辑文章按钮点击事件
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.article_edit_fab);
        fab.setOnClickListener(view -> {
            //跳转到编辑文章页面，返回来之后重新刷新列表
            Intent editArticleIntent = new Intent(ArticleActivity.this, EditArticleActivity.class);
            startActivityForResult(editArticleIntent, 7);
        });
        recyclerView = (RecyclerView) findViewById(R.id.article_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "initArticles");
        initArticles();
    }

    private void initArticles() {
        articleList.clear();
        String serverFinalUrl = MainActivity.serverUrl + "/article/getarticlelist";
        HttpClient.getClient().doPostFormRequest(serverFinalUrl, null, new HttpResultListener() {
            @Override
            protected void onResult(HttpResult httpResult) {
                if (httpResult != null && httpResult.getCode() == 200) {
                    Log.d(TAG, httpResult.toString());
                    List<ArticleInfoBase64> articleInfoBase64s = new Gson().fromJson((String) httpResult.getData(),
                            new TypeToken<List<ArticleInfoBase64>>() {
                            }.getType());
                    for (ArticleInfoBase64 articleInfoBase64 : articleInfoBase64s)
                        articleList.add(ArticleInfoBase64.toArticleInfo(articleInfoBase64));
                }
                Log.d(TAG, "共有文章数：" + articleList.size());
                ArticleActivity.this.runOnUiThread(() -> {
                    adapter = new ArticleAdapter(articleList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 销毁当前活动
                ArticleActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_OK:
                break;
        }
    }
}
package com.smlz.yisounews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.smlz.yisounews.R;
import com.smlz.yisounews.adapter.CollectedNewsListAdapter;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.entity.NewsInfo;
import com.smlz.yisounews.entity.NewsIntro;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smlz.yisounews.util.HttpResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCollectionActivity extends BaseActivity {

    public static NewsInfo curNews=null;

    private static final String TAG = "UserCollectionActivity";
    private ListView favoriteNewsList;
    private List<NewsIntro> sonNewList=new ArrayList<>();

    private List<NewsInfo> newsCollectList=new ArrayList<>();
    private Toolbar favoriteToolbar;
    private CollectedNewsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite);
        favoriteNewsList = findViewById(R.id.favorite_newsList);
        favoriteToolbar = findViewById(R.id.userFavorite_toolbar);
        favoriteToolbar.setTitle("我的收藏");
        setSupportActionBar(favoriteToolbar);

        initNews();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
    }

    // 初始化数据
    private void initNews() {
        String serverFinalUrl=MainActivity.serverUrl+"/news/getcollection";
        Map<String,String> mapCollect=new HashMap<>();
        mapCollect.put("userId",MainActivity.curUser.getUserId().toString());
        HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapCollect, new HttpResultListener() {
            @Override
            protected void onResult(HttpResult httpResult) {
                Log.d(TAG,httpResult.toString());
                if(httpResult!=null&&httpResult.getCode()==200){
                    newsCollectList=new Gson().fromJson((String)httpResult.getData(),new TypeToken<List<NewsInfo>>(){}.getType());
                    Log.d(TAG,"新闻初始化结果：条数："+newsCollectList.size());
                }
                else
                    Log.d(TAG, httpResult.getMsg());
                UserCollectionActivity.this.runOnUiThread(() -> {
                    adapter = new CollectedNewsListAdapter(UserCollectionActivity.this, R.layout.item_favorite_news,newsCollectList);
                    favoriteNewsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        favoriteNewsList.setOnItemClickListener((parent, view, position, id) -> {
            NewsActivity.curNews=newsCollectList.get(position);
            Intent intent = new Intent(UserCollectionActivity.this, NewsActivity.class);
            startActivityForResult(intent, 4);
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                UserCollectionActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 4:
                if(resultCode == RESULT_OK) {
                    initNews();
                }
                break;
        }
    }
}
package com.smlz.yisounews.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.smlz.yisounews.R;
import com.smlz.yisounews.adapter.CommentAdapter;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.entity.NewsCommentResponse;
import com.smlz.yisounews.entity.NewsInfo;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.smlz.yisounews.util.HttpResultListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//若需要启用Javascript，则抑制其警告
@SuppressLint("SetJavaScriptEnabled")
public class NewsActivity extends BaseActivity {
    private static final String TAG = "NewsActivity";
    private WebView webView;
    private Toolbar navToolbar, commentToolBar;
    private String urlData, pageUniquekey, pageTtile;
    private boolean ifCollected, ifLiked;
    List<NewsCommentResponse> comments=null;

    public static NewsInfo curNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webView);
        navToolbar = (Toolbar) findViewById(R.id.toolbar_webView);
        commentToolBar = (Toolbar) findViewById(R.id.toolbar_webComment);
        //将底部评论框的toolbar放在主界面上
        findViewById(R.id.toolbar_webComment).bringToFront();

        //TODO
        // 获取html页面的连接
        urlData = getIntent().getStringExtra("pageUrl");
//        pageUniquekey = getIntent().getStringExtra("uniquekey");
        pageTtile = getIntent().getStringExtra("news_title");


        // 通过WebView中的getSettings方法获得一个WebSettings对象
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置支持缩放
        settings.setSupportZoom(true);
        // 设置出现缩放工具。若为false，则该WebView不可缩放
        settings.setBuiltInZoomControls(true);
        // 设置允许js弹出alert对话框
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setUseWideViewPort(true);
        // 设置WebView底层的布局算法
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 设置缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        // 隐藏webview缩放按钮
        settings.setDisplayZoomControls(false);
        // 加载网页连接
        webView.loadUrl(curNews.getNewsUrl());

        setSupportActionBar(commentToolBar);
        // 设置菜单栏标题
        navToolbar.setTitle(R.string.title);
        setSupportActionBar(navToolbar);
        commentToolBar.inflateMenu(R.menu.tool_webbottom);
        commentToolBar.setTitle(R.string.title);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //若未登录则退出
                if (MainActivity.curUser == null) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    // 结束当前活动
                    Toast.makeText(NewsActivity.this, "请先登录！回退到主界面", Toast.LENGTH_LONG).show();
                    NewsActivity.this.finish();
                    return;
                }


                //向新闻表中插入
                String serverFinalUrl = MainActivity.serverUrl + "/news/initnewsinfo";
                Map<String, String> mapNews = new HashMap<>();
                mapNews.put("newsType", curNews.getNewsType());
                mapNews.put("newsTitle", curNews.getNewsTitle());
                mapNews.put("newsUrl", curNews.getNewsUrl());
                HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapNews, new HttpResultListener() {
                    @Override
                    protected void onResult(HttpResult httpResult) {
                        return;
                    }
                });
                Log.d(TAG, "新闻" + curNews.getNewsTitle() + "已传入服务器");


                // 判断是否已收藏
                MenuItem u = commentToolBar.getMenu().findItem(R.id.news_collect);
                // 页面开始加载时就去查看收藏表中是否有对应的记录
                serverFinalUrl = MainActivity.serverUrl + "/news/checkcollect";
                Map<String, String> map_collect = new HashMap<>();
                map_collect.put("userId", MainActivity.curUser.getUserId().toString());
                map_collect.put("newsTitle", curNews.getNewsTitle());
                MenuItem finalU = u;
                HttpClient.getClient().doPostFormRequest(serverFinalUrl, map_collect, new HttpResultListener() {
                    @Override
                    protected void onResult(HttpResult httpResult) {
                        if (httpResult != null && httpResult.getCode() == 200) {
                            ifCollected = true;
                        } else {
                            ifCollected = false;
                        }
                        NewsActivity.this.runOnUiThread(() -> {
                            if (ifCollected) {//已收藏
                                finalU.setIcon(R.drawable.ic_news_collected);
                            } else
                                finalU.setIcon(R.drawable.ic_news_collect);
                        });
                    }
                });


                //判断是否已点赞，更改点赞图标为news_liked或news_like
                u = commentToolBar.getMenu().findItem(R.id.news_like);
                // 页面开始加载时就去查看点赞表中是否有对应的记录
                serverFinalUrl = MainActivity.serverUrl + "/news/checklike";
                Map<String, String> map_like = new HashMap<>();
                map_like.put("userId", MainActivity.curUser.getUserId().toString());
                map_like.put("newsTitle", curNews.getNewsTitle());
                MenuItem finalU1 = u;
                HttpClient.getClient().doPostFormRequest(serverFinalUrl, map_like, new HttpResultListener() {
                    @Override
                    protected void onResult(HttpResult httpResult) {
                        if (httpResult!=null&&httpResult.getCode()==200) {
                            ifLiked = true;
                        } else {
                            ifLiked = false;
                        }
                        NewsActivity.this.runOnUiThread(() -> {
                            if (ifLiked) {//已点赞
                                finalU1.setIcon(R.drawable.ic_news_liked);
                            } else {
                                finalU1.setIcon(R.drawable.ic_news_like);
                            }
                        });
                    }
                });
            }


            // 在页面加载结束时调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 通过查看每个新闻的网页发现网页广告的div样式的选择器为body > div.top-wrap.gg-item.J-gg-item 然后去除这个样式，使其加载网页时去掉广告
                view.loadUrl("javascript:function setTop1(){document.querySelector('body > div.top-wrap.gg-item.J-gg-item').style.display=\"none\";}setTop1();");
                view.loadUrl("javascript:function setTop4(){document.querySelector('body > a.piclick-link').style.display=\"none\";}setTop4();");
                view.loadUrl("javascript:function setTop2(){document.querySelector('#news_check').style.display=\"none\";}setTop2();");
                view.loadUrl("javascript:function setTop3(){document.querySelector('body > div.articledown-wrap gg-item J-gg-item').style.display=\"none\";}setTop3();");
            }

            // 重写此方法可以让webView处理https请求。
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        // 重写执行执行去广告的js代码
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // 去除加载热点新闻
                view.loadUrl("javascript:function setTop1(){document.querySelector('body > div.top-wrap.gg-item.J-gg-item').style.display=\"none\";}setTop1();");
                view.loadUrl("javascript:function setTop4(){document.querySelector('body > a.piclick-link').style.display=\"none\";}setTop4();");
                view.loadUrl("javascript:function setTop2(){document.querySelector('#news_check').style.display=\"none\";}setTop2();");
                view.loadUrl("javascript:function setTop3(){document.querySelector('body > div.articledown-wrap gg-item J-gg-item').style.display=\"none\";}setTop3();");
            }
        });

        commentToolBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.news_share:
                    //新闻分享
                    Intent share_intent = new Intent();
                    share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                    share_intent.setType("text/plain");//设置分享内容的类型
                    share_intent.putExtra(Intent.EXTRA_SUBJECT, "新闻");//添加分享内容标题
                    share_intent.putExtra(Intent.EXTRA_TEXT, urlData);//添加分享内容
                    //创建分享的Dialog
                    share_intent = Intent.createChooser(share_intent, "share");
                    startActivity(share_intent);
                    break;
                case R.id.news_collect:
                    //点击收藏功能
                    if (MainActivity.curUser!=null) {
                        MenuItem u = commentToolBar.getMenu().findItem(R.id.news_collect);
                        String answer;
                        String serverFinalUrl = MainActivity.serverUrl + "/news/collectnews";
                        Map<String, String> map_collect = new HashMap<>();
                        map_collect.put("userId", MainActivity.curUser.getUserId().toString());
                        map_collect.put("newsTitle", curNews.getNewsTitle());
                        if (ifCollected) {
                            map_collect.put("flag","false");
                            answer = "取消收藏！";
                            ifCollected = false;
                            u.setIcon(R.drawable.ic_news_collect);
                        } else {
                            map_collect.put("flag","true");
                            ifCollected = true;
                            answer = "收藏成功！";
                            u.setIcon(R.drawable.ic_news_collected);
                        }
                        HttpClient.getClient().doPostFormRequest(serverFinalUrl, map_collect, new HttpResultListener() {
                            @Override
                            protected void onResult(HttpResult httpResult) {
                                if (httpResult!=null) {
                                    return;
                                }
                            }
                        });
                        Toast.makeText(NewsActivity.this, answer, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewsActivity.this, "请先登录后再收藏！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.news_like:
                    if (MainActivity.curUser!=null) {
                        MenuItem u = commentToolBar.getMenu().findItem(R.id.news_like);
                        String serverFinalUrl = MainActivity.serverUrl + "/news/likenews";
                        String answer = null;
                        Map<String, String> map_like = new HashMap<>();
                        map_like.put("userId", MainActivity.curUser.getUserId().toString());
                        map_like.put("newsTitle", curNews.getNewsTitle());
                        if (ifLiked) {
                            map_like.put("flag","false");
                            ifLiked = false;
                            answer = "取消点赞!";
                            u.setIcon(R.drawable.ic_news_like);
                        } else {
                            map_like.put("flag","true");
                            ifLiked = true;
                            answer = "点赞成功!";
                            u.setIcon(R.drawable.ic_news_liked);
                        }
                        HttpClient.getClient().doPostFormRequest(serverFinalUrl, map_like, new HttpResultListener() {
                            @Override
                            protected void onResult(HttpResult httpResult) {
                                return;
                            }
                        });

                        Toast.makeText(NewsActivity.this, answer, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(NewsActivity.this, "请先登录后再收藏！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.news_comment:
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NewsActivity.this);
                    View dialogView = LayoutInflater.from(NewsActivity.this).inflate(R.layout.item_comment_dialog_bottomsheet, null);

                    //RecyclerView初始化
                    String serverFinalUrl = MainActivity.serverUrl + "/news/getcomment";
                    Map<String,String> map=new HashMap<>();
                    map.put("newsTitle",curNews.getNewsTitle());
                    HttpClient.getClient().doPostFormRequest(serverFinalUrl, map, new HttpResultListener() {
                        @Override
                        protected void onResult(HttpResult httpResult) {
                            if (httpResult!=null&&httpResult.getCode()==200) {
                                comments=new Gson().fromJson((String) httpResult.getData(),
                                        new TypeToken<List<NewsCommentResponse>>() {}.getType());
                                Log.d(TAG, String.valueOf(comments.size()));
                            } else
                                Log.d(TAG, httpResult.getMsg());
                            NewsActivity.this.runOnUiThread(() -> {
                                CommentAdapter adapter = new CommentAdapter(comments, NewsActivity.this);
                                Log.d(TAG, "clearlove");
                                StaggeredGridLayoutManager verticalManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                                final RecyclerView recyclerView = dialogView.findViewById(R.id.comment_dialog_bottomsheet_comment_lists);
                                DividerItemDecoration mDivider = new
                                        DividerItemDecoration(NewsActivity.this, DividerItemDecoration.VERTICAL);
                                recyclerView.addItemDecoration(mDivider);
                                recyclerView.setLayoutManager(verticalManager);
                                recyclerView.setAdapter(adapter);
                            });
                        }
                    });

                    //评论提交Button初始化
                    Button button = dialogView.findViewById(R.id.dialog_comment_add_button);
                    String serverFinalUrl1 = MainActivity.serverUrl + "/news/commentnews";
                    button.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                        final EditText et = new EditText(NewsActivity.this);
                        builder.setTitle("请输入评论");
                        builder.setIcon(R.drawable.ic_news_comment);
                        builder.setView(et);
                        builder.setPositiveButton("提交", (dialogInterface, i) -> {
                            String content = et.getText().toString();
                            NewsCommentResponse request = new NewsCommentResponse(MainActivity.curUser.getUserId(),curNews.getNewsTitle(),content);
                            String json = new Gson().toJson(request);
                            Log.d(TAG, json);
                            HttpClient.getClient().doPostJsonRequest(serverFinalUrl1, json, new HttpResultListener() {
                                @Override
                                protected void onResult(HttpResult httpResult) {
                                    return;
                                }
                            });
                            Toast.makeText(NewsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    });

                    //退出按钮初始化
                    Button exit = dialogView.findViewById(R.id.dialog_bottomsheet_ic_close);
                    exit.setOnClickListener(view -> bottomSheetDialog.hide());
                    bottomSheetDialog.setContentView(dialogView);
                    bottomSheetDialog.show();
                    break;
                default:
                    break;
            }
            return true;
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置返回图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
    }

    //活动由不可见变为可见时调用
    @Override
    protected void onStart() {
        super.onStart();

    }

    // 活动不可见时关闭脚本，否则可能会导致oom
    @Override
    protected void onStop() {
        super.onStop();
        webView.getSettings().setJavaScriptEnabled(false);
    }

    // 初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_webtop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                NewsActivity.this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
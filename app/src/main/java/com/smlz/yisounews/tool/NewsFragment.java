package com.smlz.yisounews.tool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smlz.yisounews.R;
import com.smlz.yisounews.activity.NewsActivity;
import com.smlz.yisounews.adapter.NewsAdapter;
import com.smlz.yisounews.entity.JuHeNews;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.smlz.yisounews.entity.NewsInfo;
import com.smlz.yisounews.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//每个tab下的碎片Fragment
public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    public static String key="ef8776baa4eb1d5e48cba03dbd698cb7";


    private ListView newsListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<JuHeNews.ResultBean.DataBean> contentItems = new ArrayList<>();

    private static final int UPNEWS_INSERT = 0;

    private String currentTabName = "top";
    private int pageNo = 0, pageSize = 10;
    private FloatingActionButton fab;

    private int page=1;

    @SuppressLint("HandlerLeak")
    private Handler newsHandler = new Handler() {
        //主线程
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPNEWS_INSERT:
                    contentItems = ((JuHeNews) msg.obj).getResult().getData();
                    NewsAdapter adapter = new NewsAdapter(getActivity(), contentItems);
                    newsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_news_list, container, false);

        newsListView = (ListView) view.findViewById(R.id.newsListView);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onAttach(getContext());
        Bundle bundle = getArguments();
        final String category = bundle.getString("name", "top");
        currentTabName = category;
        fab.setOnClickListener(v -> newsListView.smoothScrollToPosition(0));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page=(page+1)%49;
            threadLoaderData(category);
        });

        getDataFromNet(category);

        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            NewsActivity.curNews=new NewsInfo(null,
                    contentItems.get(position).getUrl(),
                    contentItems.get(position).getTitle(),
                    contentItems.get(position).getCategory());
            Intent intent = new Intent(getActivity(), NewsActivity.class);
            startActivity(intent);
        });
    }

    private void threadLoaderData(final String category) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(() -> {
                getDataFromNet(category);
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }

    //异步消息处理机制
    private void getDataFromNet(final String data) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            //子线程
            @Override
            protected String doInBackground(Void... params) {
                String path = "http://v.juhe.cn/toutiao/index?type=" + data +"&page="+page+"&page_size=30&is_filter=1" +"&key="+key;
                URL url = null;
                try {
                    url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        String json = FileUtil.getStringByInputStream(inputStream,"utf-8");
                        return json;
                    }
                    else {
                        System.out.println(responseCode);
                        Log.d(TAG, "doInBackground: "+responseCode);
                        return 404 + data;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 404 + data;
            }

            protected void onPostExecute(final String result) {
                new Thread(() -> {
                    JuHeNews newsBean = null;
                    if (!result.substring(0, 3).equals("404")) {
                        newsBean = new Gson().fromJson(result, JuHeNews.class);
                        if ("0".equals("" + newsBean.getError_code())) {
                            Message msg = newsHandler.obtainMessage();
                            msg.what = UPNEWS_INSERT;
                            msg.obj = newsBean;
                            newsHandler.sendMessage(msg);
                        }
                        else {
                            threadLoaderData(currentTabName);
                        }
                    }
                    else {
                        threadLoaderData(result.substring(3));
                    }
                }).start();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        };
        task.execute();
    }
}
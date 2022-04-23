package com.smlz.yisounews.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.smlz.yisounews.R;
import com.smlz.yisounews.adapter.FeedbackAdapter;
import com.smlz.yisounews.entity.FeedbackInfo;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smlz.yisounews.util.HttpResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFeedbackActivity extends BaseActivity {

    private static final String TAG = "AdminFeedbackActivity";
    private List<FeedbackInfo> feedbacks = new ArrayList<>();
    private FeedbackAdapter adapter;
    private ListView feedbackList;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback);

        feedbackList = findViewById(R.id.admin_feedback_list);

        toolbar = findViewById(R.id.admin_feedback_toolbar);
        toolbar.setTitle("用户反馈受理");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        initFeedbacks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        feedbackList.setOnItemClickListener((adapterView, view, position, id) -> {
            FeedbackInfo data = feedbacks.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(AdminFeedbackActivity.this);
            final EditText et = new EditText(AdminFeedbackActivity.this);
            builder.setTitle("请输入反馈回复");
            builder.setIcon(R.drawable.ic_news_comment);
            builder.setView(et);
            builder.setPositiveButton("提交", (dialogInterface, i) -> {
                String serverFinalUrl = MainActivity.serverUrl + "/admin/updatefeedback";
                String repo = et.getText().toString();
                Map<String, String> mapAdmin = new HashMap<>();
                mapAdmin.put("feedbackId", data.getFeedbackId().toString());
                mapAdmin.put("feedbackRepo", repo);
                mapAdmin.put("feedbackStatus", "已解决");
                HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapAdmin, new HttpResultListener() {
                    @Override
                    protected void onResult(HttpResult httpResult) {
                        return;
                    }
                });
                Toast.makeText(AdminFeedbackActivity.this, "反馈回复成功！", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });
    }

    private void initFeedbacks() {
        String serverFinalUrl = MainActivity.serverUrl + "/admin/getfeedback";
        Map<String, String> mapAdmin = new HashMap<>();
        mapAdmin.put("adminId", "admin");
        HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapAdmin, new HttpResultListener() {
            @Override
            protected void onResult(HttpResult httpResult) {
                if (httpResult != null && httpResult.getCode() == 200) {
                    Log.d(TAG,httpResult.toString());
                    feedbacks = new Gson().fromJson((String)httpResult.getData(), new TypeToken<List<FeedbackInfo>>() {
                    }.getType());
                } else {
                    Log.d(TAG, httpResult.getMsg());
                    feedbacks = null;
                }
            AdminFeedbackActivity.this.runOnUiThread(() -> {
                adapter = new FeedbackAdapter(AdminFeedbackActivity.this, R.layout.item_feedback, feedbacks);
                feedbackList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    });
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AdminFeedbackActivity.this.finish();
                break;
        }
        return true;
    }
}

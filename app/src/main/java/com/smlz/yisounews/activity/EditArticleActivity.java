package com.smlz.yisounews.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.FileUtil;
import com.smlz.yisounews.util.HttpClient;
import com.smlz.yisounews.util.HttpResultListener;
import com.smlz.yisounews.util.ImageUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditArticleActivity extends BaseActivity {
    private ImageView editImageView;
    private EditText editTitle, editContent;
    private Button publishBtn;

    public static final int CHOOSE_ARTICLE_IMAGE = 22;
    private String editImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.article_edit_toolbar);
        toolbar.setTitle("编辑文章");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        editImageView = findViewById(R.id.article_edit_picture);
        editTitle = findViewById(R.id.article_edit_title);
        editContent = findViewById(R.id.article_edit_content);
        publishBtn = findViewById(R.id.article_edit_publish);

        editImageView.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(EditArticleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditArticleActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                openAlbum();
            }
        });

        // 发布
        publishBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(editTitle.getText()) || TextUtils.isEmpty(editContent.getText())) {
                Toast.makeText(EditArticleActivity.this, "输入不能为空！", Toast.LENGTH_SHORT).show();
            } else {

                File articleCover = editImagePath == null ? null : new File(editImagePath);
                String serverFinalUrl = MainActivity.serverUrl + "/article/insertarticle";
                Map<String,String> map=new HashMap<>();
                map.put("userId",MainActivity.curUser.getUserId().toString());
                map.put("articleTitle",editTitle.getText().toString());
                map.put("articleContent",editContent.getText().toString());
                HttpClient.getClient().doPostRequest(serverFinalUrl, Pair.create("articleCover", articleCover),
                        "image/*",
                        editTitle.getText().toString() + ".png", map, new HttpResultListener() {
                            @Override
                            protected void onResult(HttpResult httpResult) {
                                return;
                            }
                        }
                );
                Toast.makeText(EditArticleActivity.this, "文章发布成功", Toast.LENGTH_LONG).show();
                //等待服务器端同步数据
                SystemClock.sleep(1000);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                EditArticleActivity.this.finish();
            }
        });
    }

    private void openAlbum() {
        Intent mIntent = new Intent("android.intent.action.GET_CONTENT");
        mIntent.setType("image/*");
        startActivityForResult(mIntent, CHOOSE_ARTICLE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_ARTICLE_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        editImagePath= ImageUtil.handleImageOnKitKat(data,EditArticleActivity.this);
                    } else {
                        editImagePath=ImageUtil.handleImageBeforeKitKat(data,EditArticleActivity.this);
                    }
                    ImageUtil.showImage(editImageView, FileUtil.getBytesByPath(editImagePath));
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                EditArticleActivity.this.finish();
                return true;
        }
        return true;
    }
}

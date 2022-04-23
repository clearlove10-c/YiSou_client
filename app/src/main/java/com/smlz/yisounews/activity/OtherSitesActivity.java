package com.smlz.yisounews.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.smlz.yisounews.R;
import com.smlz.yisounews.base.BaseActivity;


public class OtherSitesActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_sites);
        Toolbar toolbar=(Toolbar)findViewById(R.id.other_sites_toolbar);
        toolbar.setTitle("第三方网站");
        setSupportActionBar(toolbar);
        //获取到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //将导航按钮显示出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置一个导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
        Button button1=(Button)findViewById(R.id.webSites_FengHuang);
        Button button2=(Button)findViewById(R.id.webSites_renmin);
        Button button3=(Button)findViewById(R.id.webSites_zgxinwen);
        Button button4=(Button)findViewById(R.id.webSites_zwzx);
        Button button5=(Button)findViewById(R.id.webSites_gjzwzx);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Uri uri=null;
        switch (view.getId()){
            case R.id.webSites_FengHuang:
                uri = Uri.parse("https://www.ifeng.com/");
                break;
            case R.id.webSites_renmin:
                uri = Uri.parse("http://www.people.com.cn/");
                break;
            case R.id.webSites_zgxinwen:
                uri = Uri.parse("https://www.chinanews.com/");
                break;
            case R.id.webSites_zwzx:
                uri = Uri.parse("http://cds.sczwfw.gov.cn/?areaCode=510100000000");
                break;
            case R.id.webSites_gjzwzx:
                uri = Uri.parse("http://gjzwfw.www.gov.cn/");
            default:
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 销毁当前活动
                OtherSitesActivity.this.finish();
                break;
        }
        return true;
    }
}

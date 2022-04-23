package com.smlz.yisounews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.entity.UserInfo;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.entity.UserInfoBase64;
import com.smlz.yisounews.util.FileUtil;
import com.smlz.yisounews.util.HttpClient;
import com.google.gson.Gson;
import com.smlz.yisounews.util.HttpResultListener;
import com.smlz.yisounews.util.ImageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";


    private EditText userAccount;
    private EditText userPwd;
    private Button loginBtn;
    private boolean ifLogin = false;
    private Button registerBtn;
    private String failReason;
    private Toolbar toolbar;
    private UserInfoBase64 userInfoBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userAccount = findViewById(R.id.login_userAccount);
        userPwd = findViewById(R.id.login_pwd);

        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        // 在点击编辑资料时，提醒先登录
        Intent intent = getIntent();
        String status = intent.getStringExtra("loginStatus");
        if (status != null) {
            Toast.makeText(LoginActivity.this, status, Toast.LENGTH_SHORT).show();
        }

        loginBtn = findViewById(R.id.login_on);
        registerBtn = findViewById(R.id.login_register);

        loginBtn.setOnClickListener(view -> {
            String userName = userAccount.getText().toString();
            String pwd = userPwd.getText().toString();
            // 先判断输入不能为空
            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(LoginActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
            } else {
                //判断是否为管理员
                if (userName.equals("admin")) {
                    Intent intentAdmin = new Intent(LoginActivity.this, AdminFeedbackActivity.class);
                    startActivity(intentAdmin);
                }

                //此处查询数据库，查询用户输入的账号和密码是否登录成功，其中账号是唯一标识
                String serverFinalUrl = MainActivity.serverUrl + "/user/login";
                Map<String, String> mapUser = new HashMap<>();
                mapUser.put("userName", userName);
                mapUser.put("userPwd", pwd);
                Log.d(TAG, "输入的用户名及密码： " + userName + "  " + pwd);
                HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapUser, new HttpResultListener() {
                    @Override
                    protected void onResult(HttpResult httpResult) {
                        if (httpResult.getCode() == 200) {
                            Log.d(TAG, String.valueOf(httpResult.getCode()));
                            Log.d(TAG, (String) httpResult.getData());
                            userInfoBase64 = new Gson().fromJson((String) httpResult.getData(),
                                    UserInfoBase64.class);
                            UserInfo userInfo = null;
                            try {
                                userInfo = new UserInfo(userInfoBase64.getUserId(),
                                        userInfoBase64.getUserName(),
                                        userInfoBase64.getUserPwd(),
                                        userInfoBase64.getUserSex(),
                                        userInfoBase64.getUserBirthday() == null ? null : LocalDate.parse(userInfoBase64.getUserBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                        userInfoBase64.getUserSignature(),
                                        userInfoBase64.getUserPic() == null ? null : ImageUtil.decode(userInfoBase64.getUserPic()));
                                MainActivity.curUser = userInfo;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            assert userInfo != null;
                            Log.d(TAG, userInfo.getUserName());
                            ifLogin = true;
                        } else {
                            Log.d(TAG, httpResult.getMsg());
                            failReason = "无法连接到服务器";
                            ifLogin = false;
                        }
                        LoginActivity.this.runOnUiThread(() -> {
                            if (!ifLogin) {
                                Toast.makeText(LoginActivity.this, failReason, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "登陆成功，跳转到主界面");
                                //保存账号信息到本地
                                FileUtil.saveUserBase64(userInfoBase64, LoginActivity.this);
                                Intent intent12 = new Intent(LoginActivity.this, MainActivity.class);
                                setResult(RESULT_OK, intent12);
                                finish();
                            }
                        });
                    }
                });

            }
        });

        registerBtn.setOnClickListener(view -> {
            //此处跳转到注册页面
            Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
            // 注册请求码是2
            startActivityForResult(intent1, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(LoginActivity.this, data.getStringExtra("register_status"), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (user != null)
//            save(MainActivity.curUser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}

package com.smlz.yisounews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.util.HttpClient;
import com.smlz.yisounews.util.HttpResultListener;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private EditText reg_userAccount;
    private EditText reg_userPwd;
    private EditText reg_confirm_userPwd;
    private Button registerBtn;
    private boolean ifCreated=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_userAccount = findViewById(R.id.register_userAccount);
        reg_userPwd = findViewById(R.id.register_pwd);
        reg_confirm_userPwd = findViewById(R.id.register_confirm_pwd);
        registerBtn = findViewById(R.id.register_submit);
        registerBtn.setOnClickListener(view -> {
            // 首先验证输入是否为空
            String userName = reg_userAccount.getText().toString();
            String userPwd = reg_userPwd.getText().toString();
            String secondPwd = reg_confirm_userPwd.getText().toString();
            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd) || TextUtils.isEmpty(secondPwd)) {
                // 判断字符串是否为null或者""
                Toast.makeText(RegisterActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
            }
            else {
                // 判断两次输入的密码是否匹配，匹配则写入数据库，并且结束当前活动，自动返回登录界面
                if(userPwd.equals(secondPwd)) {
                    String serverFinalUrl = MainActivity.serverUrl + "/user/register";
                    Map<String, String> mapUser = new HashMap<>();
                    mapUser.put("userName", userName);
                    mapUser.put("userPwd", userPwd);
                    Call callUser = HttpClient.getClient().doPostFormRequest(serverFinalUrl, mapUser, new HttpResultListener() {
                        @Override
                        protected void onResult(HttpResult httpResult) {
                            if (httpResult!=null&& httpResult.getCode()==200) {
                                Log.d(TAG, httpResult.toString());
                                ifCreated= true;
                            } else {
                                ifCreated = false;
                                Log.d(TAG, httpResult.getMsg());
                            }
                            RegisterActivity.this.runOnUiThread(() -> {
                                if (ifCreated)
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(RegisterActivity.this, "注册失败！该用户已被注册", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                    Intent intent = new Intent();
//                    intent.putExtra("register_status", "注册成功");
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "请确认输入密码与确认密码是否一致?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

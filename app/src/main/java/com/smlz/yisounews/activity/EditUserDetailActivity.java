package com.smlz.yisounews.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.entity.UserInfoBase64;
import com.smlz.yisounews.util.FileUtil;
import com.smlz.yisounews.util.HttpClient;
import com.smlz.yisounews.util.HttpResultListener;
import com.smlz.yisounews.util.ImageUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditUserDetailActivity extends BaseActivity {
    private ImageView userAvatar;
    private Toolbar detailToolbar;
    private String userName, userImagePath, userSignature, userSex, userPwd;
    private String userBirthday;
    public static final int CHOOSE_USER_AVATAR = 11;
    private Button buttonSubmit;
    // 定义线性布局
    private LinearLayout layout_avatar, layout_sex, layout_birth, layout_signature;
    private TextView showSex, showBirthday, showSignature;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        detailToolbar = findViewById(R.id.userData_toolbar);
        detailToolbar.setTitle("用户信息");
        setSupportActionBar(detailToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
        layout_avatar = findViewById(R.id.lay_avatar);
        layout_sex = findViewById(R.id.lay_sex);
        layout_birth = findViewById(R.id.lay_birthday);
        layout_signature = findViewById(R.id.lay_signature);

        userAvatar = findViewById(R.id.user_avatar);
        showSex = findViewById(R.id.show_sex);
        showBirthday = findViewById(R.id.show_birthday);
        showSignature = findViewById(R.id.show_sign);

        buttonSubmit = findViewById(R.id.user_info_submit);

        userName = MainActivity.curUser.getUserName();
        userBirthday = String.valueOf(MainActivity.curUser.getUserBirthday());
        userImagePath = null;
        userSignature = MainActivity.curUser.getUserSignature();
        userSex = MainActivity.curUser.getUserSex();
        userPwd = MainActivity.curUser.getUserPwd();

        initData();

        calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (MainActivity.curUser.getUserBirthday() != null) {
            try {
                date = format.parse(String.valueOf(MainActivity.curUser.getUserBirthday()));
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化数据
    private void initData() {
        showSex.setText(MainActivity.curUser.getUserSex() == null ? "待完善" : MainActivity.curUser.getUserSex());
        showBirthday.setText(MainActivity.curUser.getUserBirthday() == null ? "待完善" :
                MainActivity.curUser.getUserBirthday().toString());
        showSignature.setText(MainActivity.curUser.getUserSignature() == null ? "待完善" :
                MainActivity.curUser.getUserSignature());
        if (MainActivity.curUser.getUserPic() != null)
            ImageUtil.showImage(userAvatar, MainActivity.curUser.getUserPic());
    }

    // 在活动由不可见变为可见的时候调用
    @Override
    protected void onStart() {
        super.onStart();
        layout_avatar.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(EditUserDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditUserDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                openAlbum();
            }
        });

        layout_sex.setOnClickListener(view -> {
            String[] contentSex = new String[]{"男", "女"};
            new MaterialDialog.Builder(EditUserDetailActivity.this)
                    .title("修改性别")
                    .items(contentSex)
                    .itemsCallbackSingleChoice(userSex!=null&& userSex.equals("女") ? 1 : 0, (dialog, view1, which, text) -> {
                        userSex = text.toString();
                        showSex.setText(userSex);
                        return true;
                    })
                    .show();
        });

        layout_birth.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(EditUserDetailActivity.this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    userBirthday = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                    Log.d("UserDetailActivity", userBirthday);
                    showBirthday.setText(userBirthday);
                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        layout_signature.setOnClickListener(view -> new MaterialDialog.Builder(EditUserDetailActivity.this)
                .title("修改个性签名")
                .inputRangeRes(1, 38, R.color.colorBlack)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("请输入要修改的个性签名", userSignature, (dialog, input) -> {
                    userSignature = input.toString();
                    showSignature.setText(userSignature);
                })
                .positiveText("确定")
                .show());

        buttonSubmit.setOnClickListener(view -> {
            String serverFinalUrl=MainActivity.serverUrl+"/user/userinfoedit";
            File userPic = userImagePath == null ? null : new File(userImagePath);
            Map<String, String> map = new HashMap<>();
            map.put("userName", userName);
            map.put("userSex", userSex);
            map.put("userBirthday", userBirthday);
            map.put("userSignature", userSignature);
            HttpClient.getClient().doPostRequest(serverFinalUrl, Pair.create("userPic", userPic), "image/*",
                    MainActivity.curUser.getUserName() + ".png", map, new HttpResultListener() {
                        @Override
                        protected void onResult(HttpResult httpResult) {
                            return;
                        }
                    });
            MainActivity.curUser.setUserSex(userSex);
            MainActivity.curUser.setUserBirthday(LocalDate.parse(userBirthday, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            MainActivity.curUser.setUserSignature(userSignature);
            MainActivity.curUser.setUserPic(FileUtil.getBytesByPath(userImagePath));
            //保存用户信息到本地
            FileUtil.saveUserBase64(UserInfoBase64.toUserInfoBase64(MainActivity.curUser),EditUserDetailActivity.this);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            EditUserDetailActivity.this.finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                EditUserDetailActivity.this.finish();
                break;
        }
        return true;
    }

    private void openAlbum() {
        Intent mIntent = new Intent("android.intent.action.GET_CONTENT");
        mIntent.setType("image/*");
        startActivityForResult(mIntent, CHOOSE_USER_AVATAR);
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
            case CHOOSE_USER_AVATAR:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        userImagePath=ImageUtil.handleImageOnKitKat(data,EditUserDetailActivity.this);
                        ImageUtil.showImage(userAvatar,FileUtil.getBytesByPath(userImagePath));
                    } else {
                        ImageUtil.handleImageBeforeKitKat(data,EditUserDetailActivity.this);
                    }
                }
                break;
        }
    }
}

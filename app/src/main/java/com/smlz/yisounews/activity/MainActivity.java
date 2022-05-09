package com.smlz.yisounews.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.FeedbackInfo;
import com.smlz.yisounews.entity.HttpResult;
import com.smlz.yisounews.entity.UserInfo;
import com.smlz.yisounews.base.BaseActivity;
import com.smlz.yisounews.entity.UserInfoBase64;
import com.smlz.yisounews.tool.DataCleanManager;
import com.smlz.yisounews.util.FileUtil;
import com.smlz.yisounews.util.HttpClient;
import com.smlz.yisounews.util.HttpResultListener;
import com.smlz.yisounews.util.ImageUtil;
import com.smlz.yisounews.tool.NewsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> list;

    private TextView userNickName, userSignature;

    private ImageView userAvatar;


    public static UserInfo curUser;
    // 采用静态变量来存储当前登录的账号
    public static String currentUserId;
    //服务器IP地址
    public static String serverUrl = "http://124.221.253.19:8080/yisounews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        //获取抽屉布局实例
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //获取菜单控件实例
        navigationView = (NavigationView) findViewById(R.id.nav_design);
        //获取header布局id
        View v = navigationView.getHeaderView(0);
        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.icon_image);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        list = new ArrayList<>();
        //显示声明权限，只在manifest里声明不管用
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    //在活动由不可见变为可见的时候调用
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        //设置标题栏标题
        toolbar.setTitle(R.string.title);
        //设置自定义的标题栏实例
        setSupportActionBar(toolbar);
        //获取到ActionBar的实例
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //通过HomeAsUp来让导航按钮显示出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置Indicator来添加一个点击图标（默认图标是一个返回的箭头）
            //TODO
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //设置默认选中第一个
        navigationView.setCheckedItem(R.id.nav_edit);
        //设置菜单项的监听事件
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            //菜单页面处理
            mDrawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.nav_edit:
                    //个人信息编辑
                    if (curUser != null) {
                        Intent editIntent = new Intent(MainActivity.this, EditUserDetailActivity.class);
                        startActivityForResult(editIntent, 3);
                    } else {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                        startActivityForResult(loginIntent, 1);
                    }
                    break;
                case R.id.nav_articles:
                    // 我发布的文章
                    if (curUser!=null) {
                        Intent editIntent = new Intent(MainActivity.this, ArticleActivity.class);
                        startActivityForResult(editIntent, 6);
                    } else {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                        startActivityForResult(loginIntent, 1);
                    }
                    break;
                case R.id.nav_favorite:
                    //用户收藏
                    if (curUser!=null) {
                        Intent loveIntent = new Intent(MainActivity.this, UserCollectionActivity.class);
                        startActivity(loveIntent);
                    } else {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.putExtra("loginStatus", "请先登录后才能操作！");
                        startActivityForResult(loginIntent, 1);
                    }
                    break;
                case R.id.nav_clear_cache:
                    // 清除缓存
                    clearCacheData();
                    break;
                case R.id.nav_switch:
                    // 切换账号
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    // 登录请求码是1
                    startActivityForResult(intent, 1);
                    break;
                case R.id.nav_repo:
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("用户反馈")
                            .inputRangeRes(1, 50, R.color.colorBlack)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(null, null, (dialog, input) -> {
                                String serverFinalUrl = serverUrl + "/user/insertrepo";
                                FeedbackInfo feedbackInfo=new FeedbackInfo(MainActivity.curUser.getUserId(),input.toString());
                                String json = new Gson().toJson(feedbackInfo);
                                HttpClient.getClient().doPostJsonRequest(serverFinalUrl, json, new HttpResultListener() {
                                    @Override
                                    protected void onResult(HttpResult httpResult) {
                                        return;
                                    }
                                });
                                Toast.makeText(MainActivity.this, "反馈成功！反馈内容为：" + input, Toast.LENGTH_SHORT).show();
                            })
                            .positiveText("确定")
                            .negativeText("取消")
                            .show();
                    break;
                case R.id.nav_other_sites:
                    Intent otherSitesIntent = new Intent(MainActivity.this, OtherSitesActivity.class);
                    startActivity(otherSitesIntent);
                    break;
                default:
                    break;
            }
            return true;
        });
        //设置tab标题
        list.add("头条");
        list.add("社会");
        list.add("娱乐");
        list.add("体育");
        list.add("时尚");
        list.add("游戏");
        list.add("汽车");
        list.add("国内");
        list.add("健康");
        list.add("国际");
        list.add("军事");
        list.add("科技");
        list.add("财经");


        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            //得到当前页的标题，也就是设置当前页面显示的标题是tabLayout对应标题
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return list.get(position);
            }

            //返回position位置关联的Fragment。
            @Override
            public Fragment getItem(int position) {
                NewsFragment newsFragment = new NewsFragment();
                //判断所选的标题，进行传值显示
                //Bundle主要用于传递数据；它保存的数据，是以key-value(键值对)的形式存在的。
                Bundle bundle = new Bundle();
                if (list.get(position).equals("头条")) {
                    bundle.putString("name", "top");
                } else if (list.get(position).equals("社会")) {
                    bundle.putString("name", "shehui");
                } else if (list.get(position).equals("国内")) {
                    bundle.putString("name", "guonei");
                } else if (list.get(position).equals("国际")) {
                    bundle.putString("name", "guoji");
                } else if (list.get(position).equals("娱乐")) {
                    bundle.putString("name", "yule");
                } else if (list.get(position).equals("体育")) {
                    bundle.putString("name", "tiyu");
                } else if (list.get(position).equals("军事")) {
                    bundle.putString("name", "junshi");
                } else if (list.get(position).equals("科技")) {
                    bundle.putString("name", "keji");
                } else if (list.get(position).equals("财经")) {
                    bundle.putString("name", "caijing");
                } else if (list.get(position).equals("时尚")) {
                    bundle.putString("name", "shishang");
                } else if (list.get(position).equals("游戏")) {
                    bundle.putString("name", "youxi");
                } else if (list.get(position).equals("汽车")) {
                    bundle.putString("name", "qiche");
                } else if (list.get(position).equals("健康")) {
                    bundle.putString("name", "jiankang");
                }
                newsFragment.setArguments(bundle);
                return newsFragment;
            }

            //创建指定位置的页面视图
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                NewsFragment newsFragment = (NewsFragment) super.instantiateItem(container, position);
                return newsFragment;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return FragmentStatePagerAdapter.POSITION_NONE;
            }

            //返回当前有效视图的数量，这其实也就是将list和tab选项卡关联起来
            @Override
            public int getCount() {
                return list.size();
            }
        });

        //将TabLayout与ViewPager关联显示
        tabLayout.setupWithViewPager(viewPager);
        //绑定主界面的用户名、用户个性签名和用户头像
        View v = navigationView.getHeaderView(0);
        userNickName = v.findViewById(R.id.text_username);
        userSignature = v.findViewById(R.id.text_signature);
        userAvatar = v.findViewById(R.id.icon_image);

        //从本地文件读取上次登录用户数据
        initUserInfoFromLocal();
        if (curUser == null) {
            Log.d(TAG, "无上次登录用户信息");
            userNickName.setText("请先登录");
            userSignature.setText("请先登录");
            userAvatar.setImageResource(R.drawable.ic_default_user);
        } else {
            Log.d(TAG, "上次登录用户： " + curUser.getUserName());
            userNickName.setText(curUser.getUserName());
            if(curUser.getUserSignature()!=null)
                userSignature.setText(curUser.getUserSignature());
            if(curUser.getUserPic()!=null)
                ImageUtil.showImage(userAvatar, curUser.getUserPic());
        }
    }

    // 通过界面返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        View v = navigationView.getHeaderView(0);
        userNickName = v.findViewById(R.id.text_username);
        userSignature = v.findViewById(R.id.text_signature);

        switch (requestCode) {
            case 1: // 切换账号登录后来主界面
                Log.d(TAG, "从登陆界面返回");
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "当前用户的用户名为：" + curUser.getUserName());
                    if(curUser.getUserSignature()!=null)
                        Log.d(TAG, "当前用户的个性签名为： " + curUser.getUserSignature());
                    Log.d(TAG, "当前用户的ID为: " + curUser.getUserId());
                    if(curUser.getUserSignature()!=null)
                        userSignature.setText(curUser.getUserSignature());
                    userNickName.setText(curUser.getUserName());
                    if(curUser.getUserPic()!=null)
                        ImageUtil.showImage(userAvatar, curUser.getUserPic());
                }
                break;
            case 3: // 从个人信息返回来的数据，要更新导航栏中的数据，包括昵称，签名，图片路径
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "信息修改成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "当前用户的用户名为：" + curUser.getUserName());
                    Log.d(TAG, "当前用户的个性签名为： " + curUser.getUserSignature());
                    Log.d(TAG, "当前用户的ID为: " + curUser.getUserId());
                    userSignature.setText(curUser.getUserSignature());
                    userNickName.setText(curUser.getUserName());
                    ImageUtil.showImage(userAvatar, curUser.getUserPic());
                }
                break;
            default:
                break;
        }
    }

    //清除缓存
    public void clearCacheData() {
        File file = new File(MainActivity.this.getCacheDir().getPath());
        System.out.println("缓存目录为：" + MainActivity.this.getCacheDir().getPath());
        String cacheSize = null;
        try {
            cacheSize = DataCleanManager.getCacheSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("缓存大小为：" + cacheSize);
        new MaterialDialog.Builder(MainActivity.this)
                .title("提示")
                .content("当前缓存大小一共为" + cacheSize + "。确定要删除所有缓存？离线内容及其图片均会被清除。")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    DataCleanManager.cleanInternalCache(MainActivity.this);
                    Toast.makeText(MainActivity.this, "成功清除缓存。", Toast.LENGTH_SHORT).show();
                })
                .negativeText("取消")
                .show();
    }

    //加载标题栏的菜单布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //获取toolbar菜单项
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    //监听标题栏的菜单item的选择事件（包含服务器IP地址设置和用户账号退出
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //R.id.home修改导航按钮的点击事件为打开侧滑
            case android.R.id.home:
                //打开侧滑栏，注意要与xml中保持一致START
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.serverIPSet:
                //修改IP地址
                new MaterialDialog.Builder(MainActivity.this)
                        .title("服务器IP地址修改")
                        .inputRangeRes(1, 50, R.color.colorBlack)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(null, null, (dialog, input) -> {
                            serverUrl = input.toString();
                            Log.d(TAG, serverUrl);
                            Toast.makeText(MainActivity.this, "修改成功！修改后IP为：" + input, Toast.LENGTH_SHORT).show();
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
            case R.id.juHeKeySet:
                //修改聚合数据key
                new MaterialDialog.Builder(MainActivity.this)
                        .title("聚合KEY修改")
                        .inputRangeRes(1, 50, R.color.colorBlack)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(null, null, (dialog, input) -> {
                            NewsFragment.key = input.toString();
                            Toast.makeText(MainActivity.this, "修改成功！修改后KEY为：" + input, Toast.LENGTH_SHORT).show();
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
                break;
            case R.id.userExit:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("退出当前账号");
                builder.setIcon(R.drawable.ic_exit);
                builder.setPositiveButton("确定", (dialogInterface, i) -> {
                    curUser = null;
                    userNickName.setText("请先登录");
                    userSignature.setText("请先登录");
                    userAvatar.setImageResource(R.drawable.ic_default_user);
                    Log.d(TAG, "用户数据路径" + getFilesDir() + "/preUser");
                    if(!FileUtil.deleteFileByPath(getFilesDir() + "/preUser"))
                        Log.d(TAG, "用户数据清除失败");
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            default:
        }
        return true;
    }

    private void initUserInfoFromLocal() {
        UserInfoBase64 userInfoBase64 = null;
        FileInputStream fis = null;
        try {
            fis = openFileInput("preUser");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "preUser file not found");
            e.printStackTrace();
        }
        String preUserInfoJson = null;
        if (fis == null)
            Log.d(TAG, "preUser fis is null");
        else {
            preUserInfoJson = FileUtil.getStringByFileStream(fis);
            Log.d(TAG, preUserInfoJson);
        }
        userInfoBase64 = new Gson().fromJson(preUserInfoJson, UserInfoBase64.class);
        if (userInfoBase64 != null)
            curUser = UserInfoBase64.toUserInfo(userInfoBase64);
        else
            Log.d(TAG, "preUser is empty");
    }
}
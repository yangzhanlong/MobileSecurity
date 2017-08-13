package org.me.mobilesecurity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.AppBean;
import org.me.mobilesecurity.business.AppProvider;
import org.me.mobilesecurity.view.ProgressStateView;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class AppManagerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ProgressStateView mPsvRom;
    private ProgressStateView mPsvSD;

    private ListView mListView;

    private List<AppBean> mDatas;
    private AppAdapter mAdapter;
    private PackageUninstallReceiver mUninstallReceiver;
    private View mLoadinView;

    private int mSystemCount;
    private int mUserCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUninstallReceiver);
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);

        // 注册广播接受者,应用卸载
        mUninstallReceiver = new PackageUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);// 包卸载的行为
        filter.addDataScheme("package");
        registerReceiver(mUninstallReceiver, filter);
    }

    // 卸载广播接收者
    private class PackageUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            // dataString--> package:包名
            // 包名 --》 mDatas遍历 --》发现有包---》移除---》UI更新

            // 判断是否有相同的包名，有就删除
            String packageName = dataString.replace("package:", "");
            ListIterator<AppBean> iterator = mDatas.listIterator();
            while (iterator.hasNext()) {
                AppBean bean = iterator.next();
                if (bean.packageName.equals(packageName)) {
                    iterator.remove();
                    break;
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AppBean bean = mDatas.get(position);

        // 1. 去写内容的布局
        View contentView = View.inflate(this, R.layout.pop_app, null);

        // 2. 创建PopupWindow
        final PopupWindow window = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 额外的设置(让popupWindow只弹出一个)
        window.setFocusable(true);// 获得焦点
        window.setBackgroundDrawable(new ColorDrawable());
        window.setTouchable(true);

        // 动画
        window.setAnimationStyle(R.style.popAnimation);

        // 3. 显示PopupWindow
        //第一个参数：展示在某某控件的下方
        //第二个参数：表示x 轴的偏移量
        //第三个参数：表示y 轴的偏移量
        System.out.println("view height：" + view.getHeight());
        window.showAsDropDown(view, 200, -view.getHeight());

        TextView tvUninstall = (TextView) contentView
                .findViewById(R.id.popup_tv_unistall);
        TextView tvOpen = (TextView) contentView
                .findViewById(R.id.popup_tv_open);
        TextView tvShare = (TextView) contentView
                .findViewById(R.id.popup_tv_share);
        TextView tvInfo = (TextView) contentView
                .findViewById(R.id.popup_tv_info);

        // 获取启动项
        PackageManager pm = getPackageManager();
        final Intent intent = pm.getLaunchIntentForPackage(bean.packageName);

        // 没有启动项隐藏open
        if (intent == null) {
            tvOpen.setVisibility(View.GONE);
        }

        // 系统应用无法卸载,隐藏uninstall
        if (bean.isSystem) {
            tvUninstall.setVisibility(View.GONE);
        }

        // 卸载
        tvUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 卸载应用,发送隐式意图
                // <intent-filter>
                // <action android:name="android.intent.action.VIEW" />
                // <action android:name="android.intent.action.DELETE" />
                // <category android:name="android.intent.category.DEFAULT" />
                // <data android:scheme="package" />
                // </intent-filter>

                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);

                // 隐藏window
                window.dismiss();
            }
        });

        // 打开
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                window.dismiss();
            }
        });

        // 信息
        tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // <intent-filter>
                // <action
                // android:name="android.settings.APPLICATION_DETAILS_SETTINGS"
                // />
                // <category android:name="android.intent.category.DEFAULT" />
                // <data android:scheme="package" />
                // </intent-filter>

                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);
                window.dismiss();
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(bean.packageName, bean.packageName);
                window.dismiss();
            }
        });
    }

    private void showShare(String title, String text) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    private void initView() {
        mPsvRom = (ProgressStateView) findViewById(R.id.am_psv_rom);
        mPsvSD = (ProgressStateView) findViewById(R.id.am_psv_sd);
        mListView = (ListView) findViewById(R.id.am_listview);
        mLoadinView = findViewById(R.id.include_ll_loading);
    }

    private void initData() {
        // 去获得内部存储的总大小和剩余 (data目录)
        File dataDir = Environment.getDataDirectory();
        // 总的大小
        long romTotalSpace = dataDir.getTotalSpace();
        // 剩余的大小
        long romFreeSpace = dataDir.getFreeSpace();
        // 已使用的
        long romUsedSpace = romTotalSpace - romFreeSpace;

        int romProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);

        // 设置可用大小
        mPsvRom.setRightText(Formatter.formatFileSize(this, romFreeSpace) + "可用");
        // 设置已用大小
        mPsvRom.setLeftText(Formatter.formatFileSize(this, romUsedSpace) + "已用");
        // 设置已用进度
        mPsvRom.setProgress(romProgress);

        // 获取SD卡
        File sdDir = Environment.getExternalStorageDirectory();
        long sdTotalSpace = sdDir.getTotalSpace();// 总的
        long sdFreeSpace = sdDir.getFreeSpace();// 剩余
        long sdUsedSpace = sdTotalSpace - sdFreeSpace;// 使用的
        int sdProgress = (int) (sdUsedSpace * 100f / sdTotalSpace + 0.5f);

        // 剩余
        mPsvSD.setRightText(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
        mPsvSD.setLeftText(Formatter.formatFileSize(this, sdUsedSpace) + "已用");
        mPsvSD.setProgress(sdProgress);

        // 启动一个线程加载所有安装的应用信息
        mLoadinView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 获取所有安装的应用信息
                mDatas = AppProvider.getAllApps(AppManagerActivity.this);
                // 对所有的应用进行排序，用户程序在前面
                Collections.sort(mDatas, new Comparator<AppBean>() {
                    @Override
                    public int compare(AppBean o1, AppBean o2) {
                        boolean o1System = o1.isSystem;
                        boolean o2System = o2.isSystem;

                        int o1Int = o1System ? 1 : 0;
                        int o2Int = o2System ? 1 : 0;

                        // 小于0左边在前面
                        return o1Int - o2Int;
                    }
                });

                // 查找用户程序的个数，系统程序的个数
                for (AppBean bean : mDatas) {
                    if (bean.isSystem) {
                        mSystemCount++;
                    } else {
                        mUserCount++;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadinView.setVisibility(View.GONE);
                        // 设置 adapter
                        mAdapter = new AppAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }


    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                // 1.加载xml布局
                convertView = View.inflate(AppManagerActivity.this,
                        R.layout.item_activity_manager, null);
                // 2.新建holder
                holder = new ViewHolder();
                // 3. 设置tag
                convertView.setTag(holder);
                // 4.holder findViewbyid
                holder.ivIcon = (ImageView) convertView
                        .findViewById(R.id.item_am_iv_icon);
                holder.tvInstall = (TextView) convertView
                        .findViewById(R.id.item_am_tv_install);
                holder.tvName = (TextView) convertView
                        .findViewById(R.id.item_am_tv_name);
                holder.tvSpace = (TextView) convertView
                        .findViewById(R.id.item_am_tv_space);
            } else {
                // 有复用
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            AppBean bean = mDatas.get(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvInstall.setText(bean.isInstallSD ? "SD卡安装" : "内存安装");
            holder.tvSpace.setText(Formatter.formatFileSize(
                    AppManagerActivity.this, bean.space));

            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvInstall;
        TextView tvSpace;
    }
}

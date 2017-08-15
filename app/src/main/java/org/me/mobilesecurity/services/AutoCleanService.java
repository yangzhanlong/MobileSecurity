package org.me.mobilesecurity.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import org.me.mobilesecurity.bean.ProcessBean;
import org.me.mobilesecurity.business.ProcessProvider;

import java.util.List;

public class AutoCleanService extends Service{
    private static final String TAG = "AutoCleanService";

    private ScreenOffReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "自动清理服务开启");

        // 锁屏时自动清理服务
        // 监听锁屏

        // 注册广播
        mReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);// 锁屏的行为
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "自动清理服务关闭");

        // 注销
        unregisterReceiver(mReceiver);
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 接受到锁屏
            // 杀死进程
            List<ProcessBean> processes = ProcessProvider.getProcesses(context);
            for (ProcessBean bean : processes) {
                if (bean.pkg.packageName.equals(context.getPackageName())) {
                    // 不杀死自己
                    continue;
                }
                ProcessProvider.killProcess(context, bean.pkg.packageName);
            }
        }

    }
}

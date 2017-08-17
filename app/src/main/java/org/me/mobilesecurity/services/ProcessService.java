package org.me.mobilesecurity.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.business.ProcessProvider;
import org.me.mobilesecurity.receiver.ProcessWidgetProvider;

public class ProcessService extends Service {

    private ScreenReceiver mReceiver;
    private boolean isRunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 在锁屏时不要去更新UI，解锁屏幕时需要去更新
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_SCREEN_OFF);// 锁屏
        filter.addAction(Intent.ACTION_SCREEN_ON);// 屏幕解锁

        registerReceiver(mReceiver, filter);

        start();
    }

    private void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {
                    // 15分钟
                    ComponentName provider = new ComponentName(
                            ProcessService.this, ProcessWidgetProvider.class);
                    RemoteViews views = new RemoteViews(ProcessService.this
                            .getPackageName(), R.layout.process_widget);
                    // 1.显示进程数
                    int runningProcessCount = ProcessProvider
                            .getRunningProcessCount(ProcessService.this);
                    views.setTextViewText(R.id.process_count, "正在运行的进程数:"
                            + runningProcessCount);

                    // 2.显示可用的内存
                    long freeMemory = ProcessProvider.getTotalMemory(ProcessService.this)
                            - ProcessProvider
                            .getUsedMemory(ProcessService.this);
                    views.setTextViewText(
                            R.id.process_memory,
                            "可用内存:"
                                    + Formatter.formatFileSize(
                                    ProcessService.this, freeMemory));

                    // 3.点击事件
                    // 延期意图:打开activity，发送广播，开启服务
                    Intent intent = new Intent(ProcessService.this,
                            KillProcessService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(
                            ProcessService.this, 100, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                    AppWidgetManager.getInstance(ProcessService.this)
                            .updateAppWidget(provider, views);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        unregisterReceiver(mReceiver);
    }

    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // 判断是解锁还是锁屏
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                // 停止更新
                isRunning = false;
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                // 解锁
                // 开启更新
                start();
            }

        }

    }

}

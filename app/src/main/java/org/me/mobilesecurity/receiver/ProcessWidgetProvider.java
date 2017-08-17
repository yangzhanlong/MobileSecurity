package org.me.mobilesecurity.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import org.me.mobilesecurity.services.ProcessService;


public class ProcessWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // // 15分钟
        // ComponentName provider = new ComponentName(context,
        // ProcessWidgetProvider.class);
        // RemoteViews views = new RemoteViews(context.getPackageName(),
        // R.layout.process_widget);
        // // 1.显示进程数
        // int runningProcessCount = ProcessProvider
        // .getRunningProcessCount(context);
        // views.setTextViewText(R.id.process_count, "正在运行的进程数:"
        // + runningProcessCount);
        //
        // // 2.显示可用的内存
        // long freeMemory = ProcessProvider.getTotalMemory()
        // - ProcessProvider.getUsedMemory(context);
        // views.setTextViewText(R.id.process_memory,
        // "可用内存:" + Formatter.formatFileSize(context, freeMemory));
        //
        // // 3.点击事件
        // appWidgetManager.updateAppWidget(provider, views);

        context.startService(new Intent(context, ProcessService.class));
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, ProcessService.class));
    }
}

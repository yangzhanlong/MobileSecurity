package org.me.mobilesecurity.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Formatter;
import android.widget.Toast;

import org.me.mobilesecurity.bean.ProcessBean;
import org.me.mobilesecurity.business.ProcessProvider;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class KillProcessService extends IntentService {
    private Handler handler = new Handler();

    public KillProcessService() {
        super("KillProcessService");
        // 需要无参的构造
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 1. 是在子线程中执行的（可以执行耗时的操作）
        // 2. 多次同时调用服务,onHandleIntent会排队执行，等所有的方法调用完成后，服务就销毁
        // 3. 单次开启服务，onHandleIntent执行完成后，服务销毁

        int beforeCount = ProcessProvider.getRunningProcessCount(this);
        long beforeMemory = ProcessProvider.getUsedMemory(this);

        // 杀死进程
        List<ProcessBean> processes = ProcessProvider.getProcesses(this);
        for (ProcessBean bean : processes) {
            String packageName = bean.pkg.packageName;
            if (packageName.equals(getPackageName())) {
                continue;
            }
            ProcessProvider.killProcess(this, packageName);
        }

        int afterCount = ProcessProvider.getRunningProcessCount(this);
        long afterMemory = ProcessProvider.getUsedMemory(this);

        final int count = beforeCount - afterCount;// 杀死的进程数

        if (count > 0) {
            final long memory = beforeMemory - afterMemory;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            KillProcessService.this,
                            "杀死了"
                                    + count
                                    + "进程,节省内存"
                                    + Formatter.formatFileSize(
                                    KillProcessService.this, memory),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(KillProcessService.this, "没有可以优化的",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // try {
        // Thread.sleep(10000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
    }
}

package org.me.mobilesecurity.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";

    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "开启骚扰拦截的服务");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭骚扰拦截的服务");
    }
}

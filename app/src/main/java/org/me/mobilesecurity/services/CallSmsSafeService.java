package org.me.mobilesecurity.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import org.me.mobilesecurity.bean.BlackBean;
import org.me.mobilesecurity.db.BlackDao;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private BlackDao mDao;
    private SmsReceiver mSmsReceiver;

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

        mDao = new BlackDao(this);
        // 1. 短信的拦截
        mSmsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        // <intent-filter android:priority="1000" >
        // <!-- 接收短信的 -->
        // <action android:name="android.provider.Telephony.SMS_RECEIVED" />
        // </intent-filter>
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭骚扰拦截的服务");
        unregisterReceiver(mSmsReceiver);
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获得短信，发送者的号码,判断号码是否是需要拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                // 发送者
                String sender = sms.getOriginatingAddress();

                int type = mDao.findType(sender);
                if (type == BlackBean.TYPE_SMS || type == BlackBean.TYPE_ALL) {
                    // 需要拦截短信,不让用户接收短信
                    Log.d(TAG, "拦截" + sender + "发送的短信");
                    abortBroadcast();
                }
            }
        }
    }
}

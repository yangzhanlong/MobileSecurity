package org.me.mobilesecurity.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.me.mobilesecurity.bean.BlackBean;
import org.me.mobilesecurity.db.BlackDao;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private BlackDao mDao;
    private SmsReceiver mSmsReceiver;
    private TelephonyManager mTm;
    private CallListener mCallListener;

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

        // 2. 电话拦截
        // 1) 知道电话什么时候拨入
        // 2) 通过代码去挂电话
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mCallListener = new CallListener();
        // 注册电话的监听
        mTm.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭骚扰拦截的服务");
        // 注销短信接受者
        unregisterReceiver(mSmsReceiver);

        // 注销电话的监听
        mTm.listen(mCallListener, PhoneStateListener.LISTEN_NONE);
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


    private class CallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            // * @see TelephonyManager#CALL_STATE_IDLE:闲置状态
            // * @see TelephonyManager#CALL_STATE_RINGING:响铃状态
            // * @see TelephonyManager#CALL_STATE_OFFHOOK:摘机状态，接听电话状态
            // state
            // incomingNumber：拨入的号码

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 响铃时，正在拨入时
                int type = mDao.findType(incomingNumber);
                // 判断是否是被拦截的电话
                if (type == BlackBean.TYPE_CALL || type == BlackBean.TYPE_ALL) {
                    // 通过代码去挂断电话
                    Log.d(TAG, "通过代码去挂断" + incomingNumber + "电话");
                    //endCall(incomingNumber);
                }
            }
        }
    }

    /**
     * 挂断电话
     *
     * @param incomingNumber
     *            ：来电号码
     */
//    private void endCall(final String incomingNumber) {
//        // 1) ITelephony 实例
//        // a. ITelephony.aidl添加到代码中--》ITelephony.java
//        ITelephony iTelephony = null;
//        // ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
//
//        // IBinder ibinder = ServiceManager
//        // .getService(Context.TELEPHONY_SERVICE);
//        try {
//            // 获取到ServiceManager 的类名
//            Class<?> clazz = Class.forName("android.os.ServiceManager");
//            // 暴力反射（第一个参数：表示方法名字，第二个参数：表示方法的类型）
//            Method method = clazz.getMethod("getService", String.class);
//            // 调用当前的方法（第一个参数：表示谁调用当前方法）
//            IBinder ibinder = (IBinder) method.invoke(null,
//                    Context.TELEPHONY_SERVICE);
//            iTelephony = ITelephony.Stub.asInterface(ibinder);
//
//            // 2) ITelephony.endCall();挂断电话
//            iTelephony.endCall();// 异步操作
//            // 通过线程睡一会(不推荐)
//            // Thread.sleep(300);
//
//            // 删除通话记录
//            final ContentResolver cr = getContentResolver();
//            final Uri url = CallLog.Calls.CONTENT_URI;
//
//            // content://calls
//            // content://calls/1/100
//            // notifyForDescendents: true
//            // -->url下面的任何分支发生改变，都会通知对应的observer
//            // fasle-->只通知当前的url
//            cr.registerContentObserver(url, true, new ContentObserver(
//                    new Handler()) {
//                @Override
//                public void onChange(boolean selfChange) {
//                    String where = CallLog.Calls.NUMBER + "=?";
//                    String[] selectionArgs = new String[] { incomingNumber };
//                    cr.delete(url, where, selectionArgs);
//                    // 注销
//                    cr.unregisterContentObserver(this);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

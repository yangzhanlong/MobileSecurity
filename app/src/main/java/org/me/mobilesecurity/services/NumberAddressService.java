package org.me.mobilesecurity.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.me.mobilesecurity.db.NumberAddressDao;

import static org.me.mobilesecurity.db.NumberAddressDao.findAddress;

public class NumberAddressService extends Service {
    private CallInListten listten;
    private TelephonyManager tm;
    private CallOutReceiver mReceiver;

    public NumberAddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. 电话拨入时，显示拨入号码的归属地
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listten = new CallInListten();
        tm.listen(listten, PhoneStateListener.LISTEN_CALL_STATE);

        // 2. 电话拨出时，显示拨出号码的归属地
        // 使用广播接收者获取拨出的状态，动态注册
        mReceiver = new CallOutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listten, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(mReceiver);
    }

    // 监听电话状态
    private class CallInListten extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//
//            * @see TelephonyManager#CALL_STATE_IDLE
//            * @see TelephonyManager#CALL_STATE_RINGING
//            * @see TelephonyManager#CALL_STATE_OFFHOOK

            // 响铃时显示归属地
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                String address = findAddress(NumberAddressService.this, incomingNumber);
                Toast.makeText(NumberAddressService.this, address, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 广播接收者获取号码的归属地
    private class CallOutReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String address = NumberAddressDao.findAddress(NumberAddressService.this, number);
            Toast.makeText(NumberAddressService.this, address, Toast.LENGTH_SHORT).show();
        }
    }
}

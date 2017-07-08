package org.me.mobilesecurity.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;


public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // 判断是否开启了防盗保护
        boolean protect = PreferenceUtils.getBoolean(context, Config.KEY_SJFD_PROTECT);
        if (!protect) {
            return;
        }

        // 判断sim是否一样
        String sim = PreferenceUtils.getString(context, Config.KEY_SJFD_SIM);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currSim =tm.getSimSerialNumber() + "xxx";
        if (currSim.equals(sim)) {
            return;
        }

        // 获取安全号码
        String number = PreferenceUtils.getString(context, Config.KEY_SJFD_NUM);

        // 发送警报短信
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, "mobile is lost!!!sos", null, null);
    }
}

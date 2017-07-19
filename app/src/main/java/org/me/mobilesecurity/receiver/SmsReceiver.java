package org.me.mobilesecurity.receiver;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.services.GPSService;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver{
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // 接收短信
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            // 获取发送者
            String sender = sms.getOriginatingAddress();
            // 获取发送的内容
            String content = sms.getMessageBody();
            // 获取安全号码
            String number = PreferenceUtils.getString(context, Config.KEY_SJFD_NUM);

            // 判断发送号码是否是安全号码
            if (sender.equals(number)) {
                if ("#*location*#".equals(content)) {
                    Intent intent1 = new Intent(context, GPSService.class);
                    context.startService(intent1);
                } else if ("#*wipedata*#".equals(content)) {
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                            Context.DEVICE_POLICY_SERVICE);
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE); //清理内部和外部存储
                } else if ("#*alarm*#".equals(content)) {
                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                    player.setLooping(true); // 设置循环
                    player.setVolume(1f, 1f); // 设置声音
                    player.start();
                } else if (!"#*lockscreen*#".equals(content)
                        && content.startsWith("#*lockscreen*#")) {
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                            Context.DEVICE_POLICY_SERVICE);

                    String password = content.substring("#*lockscreen*#".length());

                    // 设置锁屏密码
                    dpm.resetPassword(password, 0);
                    // 锁屏
                    dpm.lockNow();

                } else if ("#*lockscreen*#".equals(content)) {
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                            Context.DEVICE_POLICY_SERVICE);

                    // 默认密码
                    dpm.resetPassword("123", 0);
                    dpm.lockNow();
                }
                // 不让用户看到短信内容  现在已经不能使用了
                abortBroadcast();
            }

        }
    }
}

package org.me.mobilesecurity.utils;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import java.util.List;

public class ServiceStateUtils {
    /**
     * 判断服务是否运行
     *
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(Context context,
                                           Class<? extends Service> clazz) {

        // 管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // 获取正在运行的服务
        List<ActivityManager.RunningServiceInfo> services =  am.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo info: services) {
            String serviceName = info.service.getClassName();
            // 判断是否有该服务
            if (clazz.getName().equals(serviceName)) {
                return true;
            }
        }
       return false;
    }
}

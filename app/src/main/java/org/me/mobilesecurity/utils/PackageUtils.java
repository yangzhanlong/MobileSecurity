package org.me.mobilesecurity.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;

public class PackageUtils {

    /**
     * 获得版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context)
    {
        PackageManager pm = context.getPackageManager();
        try {
            // 获得清单文件的对象
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            // 获得清单文件的对象
            PackageInfo packageInfo = pm.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取应用程序名称
     * @param context：上下文
     * @param info：包信息
     * @return
     */
    public static String getAppName(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo; // 获取节点
        return applicationInfo.loadLabel(pm).toString();
    }
    /**
     * 获取应用程序icon
     * @param context：上下文
     * @param info：包信息
     * @return
     */
    public static Drawable getAppIcon(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo;
        return applicationInfo.loadIcon(pm);
    }
    /**
     * 获取应用程序apk大小
     * @param info：包信息
     * @return
     */
    public static long getAppSpace(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;

        // applicationInfo.dataDir
        String sourceDir = applicationInfo.sourceDir;
        File file = new File(sourceDir);
        return file.length();
    }

    /**
     * 判断当前应用是否安装在sd卡
     * @param info：包信息
     * @return true:是安装在sd卡
     */
    public static boolean isInstallSD(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        int flags = applicationInfo.flags;// 应用的标记，能力或是特性

        boolean isInsallSD = false;
        if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
            isInsallSD = true;
        }
        return isInsallSD;
    }
    /**
     * 判断当前应用是否为系统应用
     * @param info：包信息
     * @return true:是系统应用
     */
    public static boolean isSystemApp(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        int flags = applicationInfo.flags;// 应用的标记，能力或是特性

        boolean isSystemApp = false;
        if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
            isSystemApp = true;
        }
        return isSystemApp;
    }
}

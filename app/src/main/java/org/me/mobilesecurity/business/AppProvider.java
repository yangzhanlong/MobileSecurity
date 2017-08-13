package org.me.mobilesecurity.business;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.me.mobilesecurity.bean.AppBean;
import org.me.mobilesecurity.utils.PackageUtils;

import java.util.ArrayList;
import java.util.List;

public class AppProvider {
    private static final String TAG = "AppProvider";
    /**
     * 获取所有已安装的应用程序
     * @param context
     * @return
     */
    public static List<AppBean> getAllApps(Context context) {
        List<AppBean> list = new ArrayList<AppBean>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        // 迭代所有的安装包
        for (PackageInfo info : packages) {
            // 获取到应用的包名
            String name = PackageUtils.getAppName(context, info);
            //获取到应用的icon
            Drawable icon = PackageUtils.getAppIcon(context, info);

            boolean isInstallSD = PackageUtils.isInstallSD(info);

            AppBean bean = new AppBean();
            bean.name = name;
            bean.icon = icon;
            bean.space = PackageUtils.getAppSpace(info);
            bean.isInstallSD = isInstallSD;
            bean.isSystem = PackageUtils.isSystemApp(info);
            Log.d(TAG, name + " : " + bean.isSystem);
            bean.packageName = info.packageName;

            list.add(bean);
        }
        return list;
    }
}

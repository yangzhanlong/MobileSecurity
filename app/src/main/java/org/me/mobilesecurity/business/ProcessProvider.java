package org.me.mobilesecurity.business;


import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import org.me.mobilesecurity.bean.PkgBean;
import org.me.mobilesecurity.bean.ProcessBean;
import org.me.mobilesecurity.utils.PackageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessProvider {
    // 获得运行的进程数
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获得正在运行的进程
        int size;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            size = getTopApp(context);
        } else {
            List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
            size = list.size();
        }
        return size;
    }

    /**
     * 6.0获取进程数
     * @param context
     * @return
     */
    private static int getTopApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                Log.i("ProcessProvider", "Running app number in last 60 seconds : " + stats.size());
                String topActivity = "";
                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                        topActivity = stats.get(j).getPackageName();
                        Log.i("ProcessProvider", "top running app is : "+topActivity);
                    }

                }
                return stats.size();
            }
        }
        return 0;
    }

    // 统计可有的进程数
    public static int getTotalProcessCount(Context context) {
        PackageManager pm = context.getPackageManager();

        // 只记录一次，重复的不会记录
        Set<String> set = new HashSet<String>();

        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            String processName = applicationInfo.processName;
            set.add(processName);

            // 所有activity节点
            ActivityInfo[] activities = info.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    set.add(activityInfo.processName);
                }
            }

            // 所有service节点
            ServiceInfo[] services = info.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    set.add(serviceInfo.processName);
                }
            }

            // 所有receiver节点
            ActivityInfo[] receivers = info.receivers;
            if (receivers != null) {
                for (ActivityInfo activityInfo : receivers) {
                    set.add(activityInfo.processName);
                }
            }

            // 所有 provider 的节点
            ProviderInfo[] providers = info.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    set.add(providerInfo.processName);
                }
            }
        }

        return set.size();
    }

    // 获取使用的内存
    public static long getUsedMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        long total = 0;
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : list) {
            // 进程id
            int pid = info.pid;
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[] { pid })[0];
            long memory = memoryInfo.getTotalPss() * 1024;
            total += memory;
        }

        return total;
    }

    // API 16
//    public static long getTotalMemory(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//
//        return memoryInfo.totalMem;
//    }

    // 获取剩余的内存
    public static long getTotalMemory(Context context) {
        File file = new File("/proc/meminfo");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String readLine = reader.readLine();
            // MemTotal: xxxxx kB
            readLine = readLine.replace("MemTotal:", "");
            readLine = readLine.replace("kB", "");
            readLine = readLine.trim();
            long result = Long.parseLong(readLine) * 1024;
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<ProcessBean> getProcesses(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ProcessBean> datas = new ArrayList<ProcessBean>();

        // 获得运行的进程
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();

        // 用来记录描述一个应用对应多个进程
        Map<String, List<ProcessBean>> map = new HashMap<String, List<ProcessBean>>();

        for (ActivityManager.RunningAppProcessInfo info : processes) {
            // 遍历进程信息
            String processName = info.processName;// 进程名称
            int pid = info.pid;
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[] { pid })[0];
            long memory = memoryInfo.getTotalPss() * 1024;

            String[] pkgList = info.pkgList;// 进程对应的应用,一个进程可以对应多个应用
            for (String pkgName : pkgList) {
                ProcessBean bean = new ProcessBean();
                bean.processName = processName;
                bean.memory = memory;
                // bean.pkg = null;
                bean.pid = pid;

                List<ProcessBean> list = map.get(pkgName);
                if (list == null) {
                    // 没有进程信息
                    list = new ArrayList<ProcessBean>();
                    map.put(pkgName, list);
                }

                list.add(bean);
            }
        }

        PackageManager pm = context.getPackageManager();

        // 循环数据
        for (Map.Entry<String, List<ProcessBean>> me : map.entrySet()) {
            String packageName = me.getKey();
            List<ProcessBean> list = me.getValue();

            try {
                PackageInfo info = null;
                info = pm.getPackageInfo(packageName, 0);

                PkgBean pkg = new PkgBean();
                pkg.icon = PackageUtils.getAppIcon(context, info);
                pkg.name = PackageUtils.getAppName(context, info);
                pkg.packageName = packageName;

                for (ProcessBean bean : list) {
                    bean.pkg = pkg;
                }

                datas.addAll(list);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return datas;
    }

    /**
     * 杀死应用的进程
     *
     * @param context
     * @param packageName
     */
    public static void killProcess(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        am.killBackgroundProcesses(packageName);
    }
}

package org.me.mobilesecurity.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.me.mobilesecurity.bean.TrafficBean;
import org.me.mobilesecurity.utils.PackageUtils;
import org.me.mobilesecurity.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficProvider {
    /**
     * 获得流量信息
     *
     * @param context
     * @return
     */
    public static List<TrafficBean> getTraffics(Context context) {
        List<TrafficBean> list = new ArrayList<TrafficBean>();

        PackageManager pm = context.getPackageManager();
        // 获得所有应用程序的发送和接收的情况
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo info : packages) {

            Drawable icon = PackageUtils.getAppIcon(context, info);
            String name = PackageUtils.getAppName(context, info);

            int uid = info.applicationInfo.uid;
            long receive = getReceive(uid);
            long send = getSend(uid);

//			if (receive == 0 && send == 0) {
//				continue;
//			}

            TrafficBean bean = new TrafficBean();
            bean.icon = icon;
            bean.name = name;
            bean.receive = receive;
            bean.send = send;

            list.add(bean);
        }

        return list;
    }

    // proc/uid_stat/xxxx/tcp_rcv -->接收的数据
    private static long getReceive(int uid) {
        String path = "/proc/uid_stat/" + uid + "/tcp_rcv";

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String readLine = reader.readLine();
            return Long.valueOf(readLine);
        } catch (Exception e) {
            return 0;
        } finally {
            StreamUtils.closeIO(reader);
        }
    }

    // proc/uid_stat/xxxx/tcp_snd --> 发送的数据
    private static long getSend(int uid) {
        String path = "/proc/uid_stat/" + uid + "/tcp_snd";

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String readLine = reader.readLine();
            return Long.valueOf(readLine);
        } catch (Exception e) {
            return 0;
        } finally {
            StreamUtils.closeIO(reader);
        }
    }
}

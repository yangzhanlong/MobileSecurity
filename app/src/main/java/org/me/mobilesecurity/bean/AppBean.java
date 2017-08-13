package org.me.mobilesecurity.bean;


import android.graphics.drawable.Drawable;

public class AppBean {
    public Drawable icon;// 应用的icon
    public String name;// 名称
    public boolean isInstallSD;// 是否安装在sd卡
    public long space;// 使用的空间大小

    public boolean isSystem;// 表示是否是系统应用

    public String packageName;// 应用的包名
}

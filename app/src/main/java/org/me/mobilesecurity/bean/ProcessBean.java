package org.me.mobilesecurity.bean;


public class ProcessBean {
    public String processName;
    public long memory;// 占用内存的大小

    public PkgBean pkg;// 进程对应的应用程序

    public int pid;// 进程id
}

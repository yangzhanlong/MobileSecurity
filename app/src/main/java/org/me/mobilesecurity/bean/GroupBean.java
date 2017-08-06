package org.me.mobilesecurity.bean;

import java.util.List;

public class GroupBean {
    public List<ChildBean> childrenDatas;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

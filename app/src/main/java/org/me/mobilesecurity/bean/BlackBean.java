package org.me.mobilesecurity.bean;


public class BlackBean {
    /**电话拦截*/
    public final static int TYPE_CALL = 0;
    /**短信拦截*/
    public final static int TYPE_SMS = 1;
    /**全部拦截*/
    public final static int TYPE_ALL = 2;

    // 电话号码
    private String number;
    // 拦截模式
    private int type;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

package org.me.mobilesecurity.bean;


public class ChildBean {
    private String name;
    private String number;

    public ChildBean() {
    }

    public ChildBean(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

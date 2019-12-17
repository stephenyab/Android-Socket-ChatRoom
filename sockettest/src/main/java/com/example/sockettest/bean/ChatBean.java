package com.example.sockettest.bean;

public class ChatBean {
    private String name;
    private String content;
    private long time;
    private int type;

    public ChatBean(String name, String content, String time, int type) {
        this.name = name;
        this.content = content;
        this.time = Long.parseLong(time);
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

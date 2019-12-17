package com.example.server.bean;

import com.example.server.util.Utils;

public class ChatBean {
    private String name;
    private String content;
    private String time;

    public ChatBean(String name, String content) {
        this.name = name;
        this.content = content;
        time = Utils.getCurrentTime();
    }
}

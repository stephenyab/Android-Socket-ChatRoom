package com.server.service;

import com.server.bean.User;
import com.server.server.UserServerImpl;
import com.server.server.UserServier;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Register implements Runnable {

    private Socket socket;
    private JSONObject jsonObject;
    private JSONObject registerJson;
    private OutputStream os;
    private UserServier userServier;
    private String msg = "";

    public Register(Socket socket, JSONObject jsonObject) throws IOException {
        this.socket = socket;
        this.jsonObject = jsonObject;
        userServier = new UserServerImpl();
    }

    @Override
    public void run() {
        System.out.println(jsonObject.toString());
        registerJson = new JSONObject();
        boolean isExisted = userServier.checkAccountExisted(jsonObject.getString("account"));
        if (isExisted) {
            registerJson.put("message", "账号已存在");
            registerJson.put("result", "fail");
        } else {
            User user = new User();
            user.setName(jsonObject.getString("account"));
            user.setPassword(jsonObject.getString("password"));
            userServier.register(user);
            registerJson.put("result", "success");
        }
        try {
            sendMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() throws IOException {
        try {
            os = socket.getOutputStream();
            os.write((registerJson.toString() + "\n").getBytes("utf-8"));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}

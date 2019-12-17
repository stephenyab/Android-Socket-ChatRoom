package com.server.service;

import com.server.bean.User;
import com.server.server.UserServerImpl;
import com.server.server.UserServier;
import com.server.util.Utils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

public class Login implements Runnable {

    private Socket socket;
    private String message;
    private JSONObject jsonObject;
    private JSONObject loginJson;
    private OutputStream os;
    private UserServier userServier;
    private String msg = "";

    public Login(Socket socket, JSONObject jsonObject) throws IOException {
        this.socket = socket;
        this.jsonObject = jsonObject;
        userServier = new UserServerImpl();
    }

    @Override
    public void run() {
        User user = new User();
        loginJson = new JSONObject();
        user.setName(jsonObject.getString("account"));
        user.setPassword(jsonObject.getString("password"));
        if (userServier.login(user)) {
            loginJson.put("result", "success");
            try {
                sendmsg(jsonObject.getString("account"));
                Service.name = jsonObject.getString("account");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loginJson.put("result", "fail");
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
            os.write((loginJson.toString() + "\n").getBytes("utf-8"));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendmsg(String name) throws IOException {
        //System.out.println(msg);
        int num = Service.sockets.size();
        for (Iterator<Socket> it = Service.sockets.iterator(); it.hasNext(); ) {
            Socket s = it.next();
            try {
                os = s.getOutputStream();
                jsonObject = new JSONObject();
                jsonObject.put("account", name);
                jsonObject.put("type", "join");
                jsonObject.put("time", System.currentTimeMillis());
                os.write((jsonObject.toString() + "\n").getBytes("utf-8"));
                System.out.println(jsonObject.toString());
            } catch (SocketException e) {
                e.printStackTrace();
                it.remove();
                System.out.println(Service.sockets);
            }
        }
    }
}
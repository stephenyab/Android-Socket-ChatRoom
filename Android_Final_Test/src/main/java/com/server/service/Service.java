package com.server.service;

import com.server.util.Utils;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class Service implements Runnable {

    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private String msg = "";
    private Thread thread;
    private JSONObject jsonObject;
    private JSONObject sendJson;
    public static String name;
    public static ArrayList<Socket> sockets = new ArrayList<>();
    public static ArrayList<String> messageList = new ArrayList<>();

    public Service(Socket socket) throws IOException {
        this.socket = socket;
        sockets.add(socket);
        sendJson = new JSONObject();

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if ((msg = br.readLine()) != null) {
                    System.out.println("接收:" + msg);
                    jsonObject = new JSONObject().fromObject(msg);
                    if (jsonObject.getString("type").equals("register")) {
                        Register register = new Register(socket, jsonObject);
                        thread = new Thread(register);
                        thread.start();
                        sockets.remove(socket);
                        thread.interrupt();
                    } else if (jsonObject.getString("type").equals("login")) {
                        Login login = new Login(socket, jsonObject);
                        thread = new Thread(login);
                        thread.start();
                        sockets.remove(socket);
                        thread.interrupt();
                    } else if (jsonObject.getString("type").equals("exit")) {
                        sockets.remove(socket);
                        br.close();
                        socket.close();
                        sendJson.put("account", name);
                        sendJson.put("type", "exit");
                        sendJson.put("time", System.currentTimeMillis());
                        msg = sendJson.toString();
                        this.sendMsg();
                        break;
                    } else if (jsonObject.getString("type").equals("message")) {
                        jsonObject.put("time", System.currentTimeMillis());
                        this.sendMsg();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() throws IOException {
        //System.out.println(msg);
        int num = sockets.size();
        for (Iterator<Socket> it = sockets.iterator(); it.hasNext(); ) {
            Socket s = it.next();
            try {
                os = s.getOutputStream();
                os.write((jsonObject.toString() + "\n").getBytes("utf-8"));
            } catch (SocketException e) {
                e.printStackTrace();
                it.remove();
                System.out.println(sockets);
            }
        }
    }

    public void sendmsg() throws IOException {
        try {
            os = socket.getOutputStream();
            os.write((msg + "\n").getBytes("utf-8"));
        } catch (SocketException e) {
            e.printStackTrace();
            sockets.remove(socket);
            System.out.println(sockets);
        }
    }
}

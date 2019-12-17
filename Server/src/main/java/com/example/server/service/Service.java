package com.example.server.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class Service implements Runnable {

    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private String msg = "";
    private static ArrayList<Socket> sockets = new ArrayList<>();

    public Service(Socket socket) {
        this.socket = socket;
        sockets.add(socket);
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
                    if (msg.equals("exit")) {
                        System.out.println("用戶:" + "已退出谈论组");
                        sockets.remove(socket);
                        msg = "退出谈论组";
                        br.close();
                        socket.close();
                        this.sendMsg();
                        break;
                    } else
                        this.sendMsg();
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
            if (socket == s)
                continue;
            try {
                os = s.getOutputStream();
                os.write((msg + "\n").getBytes("utf-8"));
            } catch (SocketException e) {
                e.printStackTrace();
                it.remove();
                System.out.println(sockets);
            }
        }
    }

    public void removeSocket() throws IOException {
        sockets.remove(socket);
        msg = "退出群聊";
        this.sendMsg();
    }
}

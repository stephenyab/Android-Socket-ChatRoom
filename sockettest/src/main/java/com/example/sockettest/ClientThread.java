package com.example.sockettest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread implements Runnable {

    private Handler handler;
    private BufferedReader br;
    private OutputStream os;
    Handler revHandler;
    private String ip;
    private int port;

    public ClientThread(Handler handler, String ip, String port) {
        this.handler = handler;
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
            new Thread(() -> {
                String content;
                try {
                    while ((content = br.readLine()) != null) {
                        Message msg = new Message();
                        msg.what = 0x123;
                        msg.obj = content;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Looper.prepare();
            revHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 0x345) {
                        try {
                            os.write((msg.obj.toString() + "\r\n").getBytes("utf-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (SocketTimeoutException el) {
            System.out.println("网络连接超时");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

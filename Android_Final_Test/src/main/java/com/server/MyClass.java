package com.server;


import com.server.service.Service;
import com.server.util.Configuration;

import java.net.ServerSocket;
import java.net.Socket;

public class MyClass {

    private static ServerSocket serverSocket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(Configuration.PORT);
            System.out.println("服务器已启动...");
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress());
                Service service = new Service(socket);
                new Thread(service).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.server;

import com.server.bean.User;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter rw;
        try {
            Socket socket = new Socket("192.168.1.107", 30001);
            osw = new OutputStreamWriter(socket.getOutputStream());
            rw = new BufferedWriter(osw);

            User user = new User();
            user.setId(88);
            user.setName("张三");
            user.setPassword("123123");
            JSONObject jsonObject = JSONObject.fromObject(user);
            rw.write(jsonObject.toString() + "\n");
            rw.close();
            socket.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

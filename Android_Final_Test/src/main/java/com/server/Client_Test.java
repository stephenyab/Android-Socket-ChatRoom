package com.server;

import com.server.server.UserServerImpl;
import com.server.server.UserServier;

import java.io.IOException;

public class Client_Test {
    public static void main(String[] args) throws IOException {
        UserServier servier = new UserServerImpl();
        System.out.println(servier.checkAccountExisted("张三"));
    }
}

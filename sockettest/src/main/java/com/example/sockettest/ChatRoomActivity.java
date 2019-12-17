package com.example.sockettest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sockettest.adapter.RecyclerChatAdapter;
import com.example.sockettest.bean.ChatBean;
import com.example.sockettest.util.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ChatRoomActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences ipPreferences;
    private ClientThread clientThread;
    private RecyclerChatAdapter adapter;
    private RecyclerView recyclerView;
    private JSONObject jsonObject;

    private String account;
    private String ip;
    private String port;

    private Message msg;

    static class MyHandler extends Handler {
        private WeakReference<ChatRoomActivity> chatRoomActivity;
        private String message;
        private ChatBean chatBean;
        private JSONObject jsonObject;

        MyHandler(WeakReference<ChatRoomActivity> chatRoomActivity) {
            this.chatRoomActivity = chatRoomActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x123) {
                try {
                    jsonObject = new JSONObject(msg.obj.toString());
                    if (jsonObject.getString("type").equals("join"))
                        chatBean = new ChatBean(jsonObject.getString("account"), "加入聊天室", jsonObject.getString("time"), Configuration.MESSAGE_SYSTEM);
                    else if (jsonObject.getString("type").equals("exit"))
                        chatBean = new ChatBean(jsonObject.getString("account"), "退出聊天室", jsonObject.getString("time"), Configuration.MESSAGE_SYSTEM);
                    else if (jsonObject.getString("type").equals("message"))
                        if (jsonObject.getString("account").equals(chatRoomActivity.get().account)) {
                            chatBean = new ChatBean(jsonObject.getString("account"), jsonObject.getString("message"), jsonObject.getString("time"), Configuration.MESSAGE_SEND);
                        } else
                            chatBean = new ChatBean(jsonObject.getString("account"), jsonObject.getString("message"), jsonObject.getString("time"), Configuration.MESSAGE_RECEIVER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                chatRoomActivity.get().adapter.addChatBean(chatBean);
                chatRoomActivity.get().recyclerView.scrollToPosition(chatRoomActivity.get().adapter.getItemCount() - 1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().hide();

        init();

        preferences = getSharedPreferences("socketInfor", Context.MODE_PRIVATE);
        EditText input = findViewById(R.id.etContent);
        Button send = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.listMessage);
        adapter = new RecyclerChatAdapter(this);
        adapter.addChatBean(new ChatBean("您已", "加入聊天室", System.currentTimeMillis() + "", Configuration.MESSAGE_SYSTEM));
        ;

        LinearLayoutManager layoutManager = new LinearLayoutManager(new MainActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ChatRoomActivity.MyHandler handler = new ChatRoomActivity.MyHandler(new WeakReference<>(this));

        clientThread = new ClientThread(handler, ip, port);
        new Thread(clientThread).start();

        send.setOnClickListener(view -> {
            msg = new Message();
            jsonObject = new JSONObject();
            try {
                jsonObject.put("account", account);
                jsonObject.put("type", "message");
                jsonObject.put("message", input.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.what = 0x345;
            msg.obj = jsonObject.toString();
            clientThread.revHandler.sendMessage(msg);
            input.setText("");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Message msg = new Message();
        msg.what = 0x345;
        jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "exit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.obj = jsonObject.toString();
        clientThread.revHandler.sendMessage(msg);
    }

    public void init() {
        preferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        ipPreferences = getSharedPreferences("socketInfor", Context.MODE_PRIVATE);
        ip = ipPreferences.getString("ip", "192.168.1.1");
        port = ipPreferences.getString("port", "30000");
        account = preferences.getString("account", null);
    }
}

package com.example.sockettest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    private EditText account;
    private EditText password;
    private ImageView login;
    private TextView register;
    private TextView chatRoomAddress;
    private SharedPreferences preferences;
    private SharedPreferences ipPreferences;
    private SharedPreferences.Editor editor;
    private ClientThread clientThread;
    private Thread thread;
    private ProgressDialog alertDialog;
    private JSONObject jsonObject;

    private String accountValue;
    private String passwordValue;
    private String ip;
    private String port;

    private final static int NORMAL = 0;
    private final static int EMPTY = 1;
    private final static int LOGINFALSE = 2;
    private final static int LOGINSUCCESS = 3;

    static class MyHandler extends Handler {
        private WeakReference<LoginActivity> loginActivity;
        private JSONObject jsonObject;

        MyHandler(WeakReference<LoginActivity> loginActivity) {
            this.loginActivity = loginActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginActivity.get().alertDialog.dismiss();
            try {
                jsonObject = new JSONObject(msg.obj.toString());
                if (jsonObject.getString("result").equals("success")) {
                    loginActivity.get().showTip("登录成功，即将进入聊天室", LOGINSUCCESS);
                } else if (jsonObject.getString("result").equals("fail")) {
                    loginActivity.get().showTip("登录失败，账号或者密码错误", LOGINFALSE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login_image);
        register = findViewById(R.id.register);
        chatRoomAddress = findViewById(R.id.chat_room_address);
        preferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        ipPreferences = getSharedPreferences("socketInfor", Context.MODE_PRIVATE);
        editor = preferences.edit();

        init();

        ip = ipPreferences.getString("ip", "192.168.1.1");
        port = ipPreferences.getString("port", "30000");

        LoginActivity.MyHandler handler = new LoginActivity.MyHandler(new WeakReference<LoginActivity>(this));
        clientThread = new ClientThread(handler, ip, port);
        thread = new Thread(clientThread);
        thread.start();

        login.setOnClickListener(view -> {
            accountValue = account.getText().toString();
            passwordValue = password.getText().toString();
            if (accountValue.equals("") || passwordValue.equals("")) {
                showTip("请完整输入账号和密码", NORMAL);
            } else {
                Message msg = new Message();
                jsonObject = new JSONObject();
                msg.what = 0x345;
                try {
                    jsonObject.put("account", accountValue);
                    jsonObject.put("password", passwordValue);
                    jsonObject.put("type", "login");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.obj = jsonObject.toString();
                clientThread.revHandler.sendMessage(msg);
                alertDialog = ProgressDialog.show(LoginActivity.this, "正在登录", "正在登录中,请稍后..."
                        , false, false);
            }
        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        chatRoomAddress.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        });

    }

    private void init() {
        accountValue = preferences.getString("account", null);
        passwordValue = preferences.getString("password", null);
        ip = ipPreferences.getString("ip", "192.168.1.1");
        port = ipPreferences.getString("port", "30000");
        if (accountValue != null)
            account.setText(accountValue);
        else
            account.setText("");

        if (passwordValue != null)
            password.setText(passwordValue);
        else
            password.setText("");
    }

    private void showTip(String tip, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示消息")
                .setMessage(tip);
        setPositiveButton(builder, type).create().show();
    }

    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder, int type) {
        return builder.setPositiveButton("确定", (dialog, which) -> {
            if (type == LOGINFALSE) {
                password.setText("");
            } else if (type == LOGINSUCCESS) {
                accountValue = account.getText().toString();
                passwordValue = password.getText().toString();
                editor.putString("account", accountValue);
                editor.putString("password", passwordValue);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                startActivity(intent);
                thread.interrupt();
                this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}

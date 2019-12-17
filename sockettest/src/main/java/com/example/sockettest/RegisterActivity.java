package com.example.sockettest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText account;
    private EditText password;
    private EditText repeatPassword;
    private Button register;
    private SharedPreferences preferences;
    private SharedPreferences ipPreferences;
    private SharedPreferences.Editor editor;
    private ClientThread clientThread;
    private Thread thread;
    private ProgressDialog alertDialog;

    private String accountValue;
    private String passwordValue;
    private String passwordRepeatValue;
    private String ip;
    private String port;
    private JSONObject registerMessage;

    private final static int NORMAL = 0;
    private final static int REPEATPASSWORDDIFFERENT = 1;
    private final static int ACCOUNTEXISTED = 2;
    private final static int REGISTERSUCCESS = 3;

    static class MyHandler extends Handler {
        private WeakReference<RegisterActivity> registerActivity;
        private JSONObject jsonObject;

        MyHandler(WeakReference<RegisterActivity> registerActivity) {
            this.registerActivity = registerActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            registerActivity.get().alertDialog.dismiss();
            try {
                jsonObject = new JSONObject(msg.obj.toString());
                if (jsonObject.getString("result").equals("fail"))
                    registerActivity.get().showTip(jsonObject.getString("message"), ACCOUNTEXISTED);
                else if (jsonObject.getString("result").equals("success")) {
                    registerActivity.get().showTip("注册成功，即将跳转到登录界面", REGISTERSUCCESS);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String message = msg.obj.toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        repeatPassword = findViewById(R.id.password_repeat);
        register = findViewById(R.id.register);
        preferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        ipPreferences = getSharedPreferences("socketInfor", Context.MODE_PRIVATE);
        editor = preferences.edit();
        ip = ipPreferences.getString("ip", "192.168.1.1");
        port = ipPreferences.getString("port", "30000");

        RegisterActivity.MyHandler handler = new RegisterActivity.MyHandler(new WeakReference<>(this));
        clientThread = new ClientThread(handler, ip, port);
        thread = new Thread(clientThread);
        thread.start();

        register.setOnClickListener(view -> {
            accountValue = account.getText().toString();
            passwordValue = password.getText().toString();
            passwordRepeatValue = repeatPassword.getText().toString();
            if (accountValue.equals("") || passwordValue.equals("") || passwordRepeatValue.equals("")) {
                showTip("请完整输入账号和密码", NORMAL);
            } else if (!passwordValue.equals(passwordRepeatValue)) {
                showTip("两次输入的密码不同，请重新输入", REPEATPASSWORDDIFFERENT);
            } else {
                Message msg = new Message();
                registerMessage = new JSONObject();
                msg.what = 0x345;
                try {
                    registerMessage.put("account", accountValue);
                    registerMessage.put("password", passwordValue);
                    registerMessage.put("type", "register");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.obj = registerMessage.toString();
                clientThread.revHandler.sendMessage(msg);
                alertDialog = ProgressDialog.show(RegisterActivity.this, "正在注册", "正在注册中,请稍后..."
                        , false, false);
            }
        });
    }

    private void showTip(String tip, int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示消息")
                .setMessage(tip);
        setPositiveButton(builder, type).create().show();
    }

    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder, int type) {
        return builder.setPositiveButton("确定", (dialog, which) -> {
            if (type == REPEATPASSWORDDIFFERENT) {
                repeatPassword.setText("");
            } else if (type == ACCOUNTEXISTED) {
                account.setText("");
            } else if (type == REGISTERSUCCESS) {
                String accountValue = account.getText().toString();
                String passwordValue = password.getText().toString();
                editor.putString("account", accountValue);
                editor.putString("password", passwordValue);
                editor.apply();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                thread.interrupt();
                this.finish();
            }
        });
    }
}

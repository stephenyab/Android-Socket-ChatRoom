package com.example.sockettest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText ipEt;
    private EditText portEt;
    private Button connectBt;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        ipEt = findViewById(R.id.ip);
        portEt = findViewById(R.id.port);
        connectBt = findViewById(R.id.connect);
        preferences = getSharedPreferences("socketInfor", Context.MODE_PRIVATE);
        editor = preferences.edit();

        init();

        connectBt.setOnClickListener(view -> {
            String ip = ipEt.getText().toString();
            String port = portEt.getText().toString();
            if (ip.equals("") || port.equals("")) {
                Toast.makeText(MainActivity.this, "ip地址或者端口号未填写完整", Toast.LENGTH_SHORT).show();
            } else {
                editor.putString("ip", ip);
                editor.putString("port", port);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                this.finish();
            }
        });
    }

    private void init() {
        String ip = preferences.getString("ip", null);
        String port = preferences.getString("port", null);
        if (ip != null)
            ipEt.setText(ip);
        else
            ipEt.setText("");

        if (portEt != null)
            portEt.setText(port);
        else
            portEt.setText("");
    }
}

package com.nuaa.codingwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SettingActivity extends AppCompatActivity {
    private LinearLayout userNameLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText userNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        userNameLayout = (LinearLayout) findViewById(R.id.user_name_layout);
        userNameEditText = (EditText)findViewById(R.id.user_name_title);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = sharedPreferences.getString("userName",null);
        if(name!=null)
            userNameEditText.setText(name);

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name  = userNameEditText.getText().toString();
                editor.putString("userName",name);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent("android.appwidget.action.MANUAL_UPDATE");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        System.out.println(intent.getAction());
    }
}

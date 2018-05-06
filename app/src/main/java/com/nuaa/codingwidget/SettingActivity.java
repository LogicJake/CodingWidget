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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText userNameEditText,githubUserNameEditText;
    private boolean flag = false;
    private boolean flag2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        userNameEditText = (EditText)findViewById(R.id.user_name_title);
        githubUserNameEditText = (EditText)findViewById(R.id.github_user_name_title);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = sharedPreferences.getString("userName",null);
        if(name!=null)
            userNameEditText.setText(name);

        name = sharedPreferences.getString("githubUserName",null);
        if(name!=null)
            githubUserNameEditText.setText(name);

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name  = userNameEditText.getText().toString();
                editor.putString("userName",name);
                editor.commit();
                flag = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        githubUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name  = githubUserNameEditText.getText().toString();
                editor.putString("githubUserName",name);
                editor.commit();
                flag2 = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (flag){
            Intent intent = new Intent("android.appwidget.action.MANUAL_UPDATE");
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        }
        if (flag2){
            Intent intent = new Intent("android.appwidget.action.MANUAL_UPDATE2");
            intent.setPackage(getPackageName());
            sendBroadcast(intent);
        }
    }
}

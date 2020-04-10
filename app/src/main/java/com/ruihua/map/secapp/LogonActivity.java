package com.ruihua.map.secapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LogonActivity extends AppCompatActivity {
    public final static String LOGONNAME_MESSAGE = "133";
    public String LogonName="133";
    private EditText mobileText;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);
    }

    public void LogonClick(View view) {
        try
        {
            Intent intent = new Intent(this, EmptyActivity.class);//显示intent
            intent.putExtra(LOGONNAME_MESSAGE, LogonName);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


        /////验证用户和密码
//        if (!CheckText()) {
//            return;
//        }
//        mobileText = (EditText) findViewById(R.id.editLogon);
//        String LogonName = mobileText.getText().toString();
//
//        mobileText = (EditText) findViewById(R.id.editPass);
//        String pass = mobileText.getText().toString();
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("LogonName", LogonName);
//        params.put("Pass", pass);
//
//        String strUrlPath = "http://218.242.28.98:29583/wl/getUserInfo.php";
//        String strResult = HttpUtils.submitPostData(strUrlPath, params, "utf-8");
//
//        String m = strResult.toString().trim();
//        String n = "true";
//
//        if (m.equalsIgnoreCase(n)) {
//            text = (TextView) findViewById(R.id.viewHint);
//            text.setText("验证信息正确");
//
//            // 传递参数logonname到下个页面
//            Intent intent = new Intent(this, EmptyActivity.class);//显示intent
//            intent.putExtra(LOGONNAME_MESSAGE, LogonName);
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(this, "手机账号或密码错误", 1).show();
//        }

    }

    private boolean CheckText() {
        boolean result = true;
        String number = "";
        mobileText = (EditText) findViewById(R.id.editLogon);
        number = mobileText.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "账号不能为空", 1).show();
            result = false;
        }

        number = "";
        mobileText = (EditText) findViewById(R.id.editPass);
        number = mobileText.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "密码不能为空", 1).show();
            result = false;
        }

        return result;
    }
    public void RegClick(View view) {

        //gotoApplyCar();
    }
}

package com.ruihua.map.secapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动MyService，形成一个循环，这样每10秒钟后MyService都会执行一次。这样一个长期的后台定时服务就完成了。
        Log.i("this is receive ","time:"+ new Date().toString());

        Intent i = new Intent(context,MyService.class);
        context.startService(i);

        //test
    }

}

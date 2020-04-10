package com.ruihua.map.secapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MyService extends Service {
    private String fileName ="test.txt";
    boolean WarnIsNotEmpty=true;
    Context context;
    public String PubContentText="点击查看详细内容-车船号最高电压最低电压最高温度最低温度等信息";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context=this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.d("MyService","executed at"+new Date().toString());
                Log.i("MyService","executed at"+ new Date().toString());

                Proc();

                if  (WarnIsNotEmpty) {
                    setNotification();
                }
            }
        }).start();
        //获取AlarmManager 实例
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 5*60*1000;
        //触发时间
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        //ELAPSED_REALTIME_WAKEUP表示定时任务的触发时间从系统开机开始算起，但会唤醒CPU。在10秒钟后就会执行AlarmReceiver中的onReceive方法
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    /**
     *  要定时执行的事件
     */
    public void Proc(){
        Log.i("test","executed at"+ new Date().toString());

        String tempstr= GetWarnInfo();
        tempstr=tempstr+" time:"+new Date().toString();
        String oldStr=ReadFromText(fileName);
        tempstr=tempstr+"\n"+oldStr;
        SavetoTxtFile(tempstr);
    }

    /**
     * http://218.242.28.98:29583/wl/GetBoatWarnInfo.php?USER_ID=133  获取报警信息测试例子
     * http://218.242.28.98:29583/wl/GetBoatWarnInfo.php?USER_ID=133&MAX_DY=3.47&MIN_DY=2.6&MAX_WD=45&MIN_WD=8
     */
    public String GetWarnInfo(){
        String result = "";
        String sAll="";
        try {

            SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
            String strMaxDY = preferences.getString("strMaxDY", "");
            String strMinDY = preferences.getString("strMinDY", "");
            String strMaxWD = preferences.getString("strMaxWD", "");
            String strMinWD = preferences.getString("strMinWD", "");

            String sUserID= preferences.getString("USER_ID", "");
            String sUserName= preferences.getString("USER_NAME", "");

            Map<String, String> params = new HashMap<String, String>();
            params.put("USER_ID", sUserID);
            params.put("MAX_DY", strMaxDY);
            params.put("MIN_DY", strMinDY);
            params.put("MAX_WD", strMaxWD);
            params.put("MIN_WD", strMinWD);
            String strUrlPath = "http://218.242.28.98:29583/wl/GetBoatWarnInfo.php";
            String all = "";
            String strCarInfo = HttpUtils.submitPostData(strUrlPath, params, "utf-8");

            JSONArray jsonArray = new JSONArray(strCarInfo);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sEV_NAME = jsonObject.getString("EV_NAME");
                String sVOL_MAX = jsonObject.getString("VOL_MAX");
                String VOL_MAX_CASE = jsonObject.getString("VOL_MAX_CASE");
                String VOL_MAX_CELL = jsonObject.getString("VOL_MAX_CELL");
                String VOL_MIN = jsonObject.getString("VOL_MIN");
                String VOL_MIN_CASE = jsonObject.getString("VOL_MIN_CASE");
                String VOL_MIN_CELL = jsonObject.getString("VOL_MIN_CELL");
                String TEMP_MAX = jsonObject.getString("TEMP_MAX");
                String TEMP_MAX_CASE = jsonObject.getString("TEMP_MAX_CASE");
                String TEMP_MAX_CELL = jsonObject.getString("TEMP_MAX_CELL");
                String TEMP_MIN = jsonObject.getString("TEMP_MIN");
                String TEMP_MIN_CASE = jsonObject.getString("TEMP_MIN_CASE");
                String TEMP_MIN_CELL = jsonObject.getString("TEMP_MIN_CELL");

               sAll=sAll+sEV_NAME+"最高电压"+sVOL_MAX+"箱"+VOL_MAX_CASE+"节"+VOL_MAX_CELL+"最低电压"+VOL_MIN
                       +"箱"+VOL_MIN_CASE+"节"+VOL_MIN_CELL+"最高温度"+TEMP_MAX+"箱"+TEMP_MAX_CASE+"节"+TEMP_MAX_CELL
                       +"最低温度"+TEMP_MIN+"箱"+TEMP_MIN_CASE+"节"+TEMP_MIN_CELL+"\n";
            }
            if (jsonArray.length()>0){
                WarnIsNotEmpty=true;
            }else
            {
                WarnIsNotEmpty=false;
            }
            result=sAll;
            PubContentText="点击查看详细内容.."+sAll;

        } catch (Exception e) {

        }

        return result;
    }


    //数据保存到文本
    public void SavetoTxtFile(String  strText) {
        try {
            FileOutputStream outStream = this.openFileOutput(fileName,MODE_PRIVATE);
            // 将文本转换为字节集
            byte[] data = strText.getBytes();
            try {
                // 写出文件
                outStream.write(data);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从文本读取数据
     */
    private String ReadFromText (String fileName) {
        String result="";

        FileInputStream inputStream;
        byte[] buffer = null;
        try {
            inputStream = this.openFileInput(fileName);
            try {
                // 获取文件内容长度
                int fileLen = inputStream.available();
                // 读取内容到buffer
                buffer = new byte[fileLen];
                inputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 返回文本信息
        if (buffer != null){
            try {
                //result= EncodingUtils.getString(buffer, "utf-8");
                result=new String(buffer, "UTF-8");//用new String可以运行在任意API Level
             } catch (Exception ex) {

            }
        }
        else
            result= "";

        return result;
    }

    /**
     * 发送消息通知
     */
    public void setNotification(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder1 = new Notification.Builder(MyService.this);
        builder1.setSmallIcon(R.mipmap.back_icon); //设置图标
        builder1.setTicker("电压电流报警通知");
        builder1.setContentTitle("通知"); //设置标题

        builder1.setContentText(PubContentText); //消息内容
        builder1.setWhen(System.currentTimeMillis()); //发送时间
        builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent = new Intent(this, SetupActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(MyService.this, 0, intent, 0);
        builder1.setContentIntent(pendingIntent);
        Notification notification1 = builder1.build();

        notificationManager.notify(124, notification1); // 通过通知管理器发送通知

    }

}

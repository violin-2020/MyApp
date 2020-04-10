package com.ruihua.map.secapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SetupActivity extends AppCompatActivity {
    private String fileName ="test.txt";
    private String PubMessage="";
    private String HisDate="";
    Context context;
    EditText dyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;

        setContentView(R.layout.activity_setup);

        GetValues();

        PubMessage= ReadFromText(fileName);
        TextView text = (TextView) findViewById(R.id.ViewText);
        text.setText(PubMessage);

        ClearTextProc();
    }

    /**
     * 根据日期来判断是否要清空记录
     */
    private void ClearTextProc() {
        HisDate=ReadFromText("myDate.txt");

        Calendar calendar = Calendar.getInstance();
        String mYear=String.valueOf(calendar.get(Calendar.YEAR));
        String mMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);        //获取日期的月
        String mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String curDate=mYear+mMonth+mDay;
        if (curDate.equals(HisDate))
        {

        }
        else{
            WriteDateToText(curDate);
            SavetoTxtFile(" ");
        }

    }

    /**
     * 记录日期到文本
     * @param strText
     */
    public void WriteDateToText(String  strText) {
        try {
            FileOutputStream outStream = this.openFileOutput("myDate.txt",MODE_PRIVATE);
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
     * 数据保存到文本
     */
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
        if (buffer != null) {
            try {
                //result= EncodingUtils.getString(buffer, "utf-8");
                result = new String(buffer, "UTF-8");//用new String可以运行在任意API Level
            } catch (Exception ex) {
            }
        }
        else
            result= "";

        return result;
    }


    public void ServiceClick(View view){

        /*如果服务正在运行，直接return*/
        if (isServiceRunning("com.ruihua.map.secapp.MyService")){
            Log.i("服务正在运行","return");
            TextView text = (TextView) findViewById(R.id.ViewText);
            text.setText("服务正在运行..");
            return;
        }
        /*启动服务*/
        Intent startServiceIntent = new Intent(this, MyService.class);
        startService(startServiceIntent);



    }

    public void StopClick(View view){
        /*停止服务*/
//        if (isServiceRunning("com.ruihua.map.secapp.MyService")){
//            Log.i("停止服务","return");
//            Intent stopIntent = new Intent(this,MyService.class);
//            stopService(stopIntent);//停止服务
//            TextView text = (TextView) findViewById(R.id.ViewText);
//            text.setText("停止服务..");
//        }

        Intent intent = new Intent(this, GridViewActivity.class);//显示intent
        startActivity(intent);
    }
    /**
     * 判断服务是否运行
     */
    private boolean isServiceRunning(final String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }

    //保存参数值
    public void buttonSaveClick(View view){

        dyText = (EditText) findViewById(R.id.editMaxDY);
        String strMaxDY = dyText.getText().toString();

        dyText = (EditText) findViewById(R.id.editMinDY);
        String strMinDY = dyText.getText().toString();

        dyText = (EditText) findViewById(R.id.editMaxWD);
        String strMaxWD = dyText.getText().toString();

        dyText = (EditText) findViewById(R.id.editMinWD);
        String strMinWD = dyText.getText().toString();

        SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("strMaxDY", strMaxDY);
        editor.putString("strMinDY", strMinDY);
        editor.putString("strMaxWD", strMaxWD);
        editor.putString("strMinWD", strMinWD);

        editor.commit();

    }

    //获取保存的参数值
    private void GetValues(){

        try {
            SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
            String strMaxDY = preferences.getString("strMaxDY", "");

            dyText = (EditText) findViewById(R.id.editMaxDY);
            dyText.setText(strMaxDY);

            String strMinDY = preferences.getString("strMinDY", "");
            dyText = (EditText) findViewById(R.id.editMinDY);
            dyText.setText(strMinDY);

            String strMaxWD = preferences.getString("strMaxWD", "");
            dyText = (EditText) findViewById(R.id.editMaxWD);
            dyText.setText(strMaxWD);

            String strMinWD = preferences.getString("strMinWD", "");
            dyText = (EditText) findViewById(R.id.editMinWD);
            dyText.setText(strMinWD);
        } catch (Exception ex) {

        }


    }
}

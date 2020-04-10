package com.ruihua.map.secapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BoatInfoActivity extends AppCompatActivity {
    private Context context;
    public String BoatName="瑞华一号";
    public String EV_ID="1157627968";
    private Double Jingdu=31.305246;
    private Double Weidu=121.279255;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;//保障context能保存获取临时数据正常成功
        setContentView(R.layout.activity_boat_info);

        //获取网页传递的参数
        Intent intent = getIntent();
        BoatName=intent.getStringExtra(EmptyActivity.BOATNAME_MESSAGE);
        EV_ID=intent.getStringExtra(EmptyActivity.EV_ID_MESSAGE);
        TextView text = (TextView) findViewById(R.id.viewMessage);
        text.setText(BoatName+EV_ID);

        GetBoatInfo();
    }

    public void DcInfoClick(View view){

        GetDcInfo();
    }

    /**
     * 获取电池信息
     */
    private  void GetDcInfo(){
        text = (TextView) findViewById(R.id.BoatMessage);
        text.setText("");

        String strUrlPath = "http://218.242.28.100:8081/new/mono_his_test3.php";
        String sEv_ID = EV_ID;
        Map<String, String> params = new HashMap<String, String>();
        //Map<String, Long> params = new HashMap<String, Long>();
        params.put("EV_ID",sEv_ID);
        params.put("DATE_VAL","2019-03-08");
        params.put("HOUR_VAL","0");
        params.put("MIN_VAL","30");

        String all = "";
        String strCarInfo = HttpUtils.submitPostData(strUrlPath, params,
                "utf-8");
        all=strCarInfo;

        text = (TextView) findViewById(R.id.BoatMessage);
        text.setText(all);

    }

    //保存参数
    private void SaveEV_ID(String ev_id){
        SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("stringEV_ID", ev_id);
        editor.commit();
    }

    public void BoatInfoClick(View view){

        GetBoatInfo();
    }

    /**
     * 获取综合信息
     */
    private  void GetBoatInfo(){

        try {

            Map<String, String> params = new HashMap<String, String>();
            params.put("EV_NAME", BoatName);
            String strUrlPath = "http://218.242.28.98:29583/wl/GetCarInfoFromName.php";
            String all = "";
            String strCarInfo = HttpUtils.submitPostData(strUrlPath, params, "utf-8");

            JSONArray jsonArray = new JSONArray(strCarInfo);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sEV_NAME = jsonObject.getString("EV_NAME");
                String sEV_NUM = jsonObject.getString("EV_NUM");
                // int sEV_TYPE = jsonObject.getInt("EV_TYPE");
                String sMANUFCTUR = jsonObject.getString("MANUFCTUR");
                String sCOMMENT = jsonObject.getString("COMMENT");
                // String sBATT_TYPE = jsonObject.getString("BATT_TYPE");
                String sAC_TYPE = jsonObject.getString("AC_TYPE");
                String sMOBILE_NUM = jsonObject.getString("MOBILE_NUM");
                String sFIRST_USE = jsonObject.getString("FIRST_USE");
                String sSTATUS = jsonObject.getString("STATUS");

                if (sSTATUS.equals("run")) {
                    sSTATUS = "运行";
                } else if (sSTATUS.equals("stop")) {
                    sSTATUS = "停止";
                } else {
                    sSTATUS = "其他";
                }

                Double sVOLTAGE = jsonObject.getDouble("VOLTAGE");
                Double sCURRENT = jsonObject.getDouble("CURRENT");

                Double sVOL_MAX = jsonObject.getDouble("VOL_MAX");
                Double sVOL_MIN = jsonObject.getDouble("VOL_MIN");
                Double sTEMP_MAX = jsonObject.getDouble("TEMP_MAX");
                Double sTEMP_MIN = jsonObject.getDouble("TEMP_MIN");
                Integer sVOL_MAX_CASE = jsonObject.getInt("VOL_MAX_CASE");
                Integer sVOL_MAX_CELL = jsonObject.getInt("VOL_MAX_CELL");
                Integer sVOL_MIN_CASE = jsonObject.getInt("VOL_MIN_CASE");
                Integer sVOL_MIN_CELL = jsonObject.getInt("VOL_MIN_CELL");
                Integer sTEMP_MAX_CASE = jsonObject.getInt("TEMP_MAX_CASE");
                Integer sTEMP_MAX_CELL = jsonObject.getInt("TEMP_MAX_CELL");
                Integer sTEMP_MIN_CASE = jsonObject.getInt("TEMP_MIN_CASE");
                Integer sTEMP_MIN_CELL = jsonObject.getInt("TEMP_MIN_CELL");


                Double sSPEED = jsonObject.getDouble("SPEED");
                String sLAST_ONLINE = jsonObject.getString("LAST_ONLINE");
                Double sMILEAGE = jsonObject.getDouble("MILEAGE");
                Double sGPS_LAT = jsonObject.getDouble("GPS_LAT");
                Weidu = sGPS_LAT;
                Double sGPS_LNG = jsonObject.getDouble("GPS_LNG");
                Jingdu = sGPS_LNG;
                String sONLINE = jsonObject.getString("ONLINE");
                String sCOMP_NAME = jsonObject.getString("COMP_NAME");

                all = all + "名称:" + sEV_NAME + "\n车牌号:" + sEV_NUM + "\n生产厂家:"
                        + sMANUFCTUR + "\n路线:" + sCOMMENT + "\n手机号:"
                        + sMOBILE_NUM + "\n空调类型:" + sAC_TYPE + "\n上线日期:"
                        + sFIRST_USE + "\n状态:" + sSTATUS + "\n总电压:" + sVOLTAGE
                        + "\n总电流:" + sCURRENT + "\n速度:" + sSPEED + "\n最后在线日期:" + sLAST_ONLINE
                        + "\n最高电压:" + sVOL_MAX + " 箱:" + sVOL_MAX_CASE + " 节:" + sVOL_MAX_CELL
                        + "\n最低电压:" + sVOL_MIN + " 箱:" + sVOL_MIN_CASE + " 节:" + sVOL_MIN_CELL
                        + "\n最高温度:" + sTEMP_MAX + " 箱:" + sTEMP_MAX_CASE + " 节:" + sTEMP_MAX_CELL
                        + "\n最低温度:" + sTEMP_MIN + " 箱:" + sTEMP_MIN_CASE + " 节:" + sTEMP_MIN_CELL
                        + "\n经纬度:" + sGPS_LAT + "-" + sGPS_LNG + "\n里程:" + sMILEAGE
                        + "\n是否在线:" + sONLINE + "\n公司名称:" + sCOMP_NAME;

            }
            if (TextUtils.isEmpty(all)) {
                all = "未查到信息";
            }

            text = (TextView) findViewById(R.id.BoatMessage);
            text.setText(all);
        } catch (Exception e) {

        }

    }
}

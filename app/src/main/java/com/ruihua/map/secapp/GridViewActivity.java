package com.ruihua.map.secapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GridViewActivity extends AppCompatActivity {

    private GridView mAppGridView = null;
    // 应用图标
//    private int[] mAppIcons = {
//            R.drawable.r03, R.drawable.r03, R.drawable.r03,
//            R.drawable.x54 , R.drawable.x54, R.drawable.x54,
//            R.drawable.r05, R.drawable.r05, R.drawable.r05
//    };
    private int[] mAppIconsNew ;
    private String[]  mStringName;
    // 应用名
//    private String[] mAppNames = {
//            "魔法棒", "点赞社群", "购物街区","蚂蚁社区","鑫鱻地图",
//            "鑫鱻消息", "房品汇","商城","模型盒子"
//    };
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_grid_view);

        GetBoatName();

        // 获取界面组件
        mAppGridView = (GridView) findViewById(R.id.gridview);

        // 初始化数据，创建一个List对象，List对象的元素是Map
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

//        for (int i = 0; i < mAppIcons.length; i++) {
//            Map<String, Object> listItem = new HashMap<String, Object>();
//            listItem.put("icon", mAppIcons[i]);
//            listItem.put("name",mAppNames[i]);
//            listItems.add(listItem);
//        }

        for (int i = 0; i < mAppIconsNew.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("icon", mAppIconsNew[i]);
            listItem.put("name",mStringName[i]);
            listItems.add(listItem);
        }

        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                listItems,
                R.layout.gridview_item,
                new String[]{"icon", "name"},
                new int[]{R.id.icon_img, R.id.name_tv});

        // 为GridView设置Adapter
        mAppGridView.setAdapter(simpleAdapter);

        // 添加列表项被单击的监听器
        mAppGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 显示被单击的图片
                Toast.makeText(GridViewActivity.this, mStringName[position],
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GetBoatName(){

       String UserID= GetUserID();
        String url = "http://218.242.28.98:29583/wl/GetCarInfoJsonChuan.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("USER_ID", UserID);
        String strResult = HttpUtils.submitPostData(url, params, "utf-8");
        //
        try {
            JSONArray jsonArray = new JSONArray(strResult);
            mStringName=new String[jsonArray.length()];
            mAppIconsNew=new int[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sEV_ID = jsonObject.getString("EV_ID");
                String sEV_NAME = jsonObject.getString("EV_NAME");
                mStringName[i]=sEV_NAME;
                mAppIconsNew[i]=GetIntFromName(sEV_NAME);
                //mAppIconsNew[i]=R.drawable.r03;

            }
        } catch (Exception e) {

        }

    }

    private String GetUserID() {
        String result="133";
        try {
            SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
             result= preferences.getString("USER_ID", "");
            //String sUserName= preferences.getString("USER_NAME", "");
        } catch (Exception e) {

        }
        return result;
    }

    private int GetIntFromName(String sEV_name) {
        int result=R.drawable.r03;

        switch (sEV_name){
            case "RHY-03" :result=R.drawable.r03;break;
            case "RHY-04" :result=R.drawable.r04new ;break;
            case "RHY-05" :result=R.drawable.r05;break;
            case "海巡艇06" :result=R.drawable.x06;break;
            case "500吨-07" :result=R.drawable.x500;break;
            case "RHY-08" :result=R.drawable.r08;break;
            case "瑞华一号" :result=R.drawable.x54;break;
            case "GZ广州-2000" :result=R.drawable.gz2000;break;
            case "朔.杨堤0071" :result=R.drawable.x071;break;

            default:result=R.drawable.r03;break;

        }

        return result;
    }
}

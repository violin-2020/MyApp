package com.ruihua.map.secapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptyActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AbsListView.OnScrollListener{

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> dataList;
    private String USER_ID="133";
    private String USER_Name="瑞华集团";
    public final static String BOATNAME_MESSAGE = "瑞华一号";
    public final static String EV_ID_MESSAGE = "1157627968";
    public String BoatName="瑞华一号";
    public String EV_ID="1157627968";
    public Map<String,String> HXEV_ID= new HashMap<String,String>();
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;// 保障context能保存获取临时数据正常成功
        setContentView(R.layout.activity_empty);

        //获取网页传递的参数
        Intent intent = getIntent();
        String sUSER_ID=intent.getStringExtra(LogonActivity.LOGONNAME_MESSAGE);
        if (!TextUtils.isEmpty(sUSER_ID)){
            USER_ID=sUSER_ID;
        }
        USER_Name=GetUserName(USER_ID);
        SaveUserIDUserName(USER_ID,USER_Name);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText(USER_ID+USER_Name);

        GetBoatInfo();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = listView.getItemAtPosition(position) + "";
        BoatName= text.substring(0,text.indexOf("/"));
        if (HXEV_ID.containsKey(BoatName)){
            EV_ID=HXEV_ID.get(BoatName);
            TextView tv= (TextView) findViewById(R.id.textView);
            tv.setText(USER_ID+USER_Name+";"+BoatName+EV_ID);
        }
        //Toast.makeText(this,"position=" + position + "\ntext=" + text,Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        switch (scrollState) {
//            case SCROLL_STATE_FLING:
//                Log.i("Main","用户在手指离开屏幕之前，由于用力滑了一下，视图仍在滑动");
//                Map<String,Object> map = new HashMap<String,Object>();
//                map.put("pic",R.drawable.wolf);
//                map.put("text","增加项");
//                dataList.add(map);
//                simpleAdapter.notifyDataSetChanged();
//                break;
//            case SCROLL_STATE_IDLE:
//                Log.i("Main","视图已经停止滑动");
//                break;
//            case SCROLL_STATE_TOUCH_SCROLL:
//                Log.i("Main","手指没有离开屏幕，视图正在滑动");
//                break;
//        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * 保存用户名和ID
     * @param user_id
     * @param user_name
     */
    public void SaveUserIDUserName(String user_id,String user_name){

        SharedPreferences preferences = context.getSharedPreferences("itcast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USER_ID", user_id);
        editor.putString("USER_NAME", user_name);
        editor.commit();
    }

    //定位地图位置
    public void NewMapClick(View view) {
        Intent intent = new Intent(this, NewMapActivity.class);//显示intent
        intent.putExtra(BOATNAME_MESSAGE, BoatName);
        startActivity(intent);
    }

    //切换到车船电池等信息页面
    public void BoatInfoClick(View view) {
        Intent intent = new Intent(this, BoatInfoActivity.class);//显示intent
        intent.putExtra(BOATNAME_MESSAGE, BoatName);
        intent.putExtra(EV_ID_MESSAGE, EV_ID);
        startActivity(intent);
    }

    public void buttonPicClick(View view) {

        listView = (ListView) findViewById(R.id.list);
        //1.新建数据适配器
        /**
         * SimpleAdapter(context,data,resource,from,to)
         * context : 上下文
         * data : 数据源 List<? extends Map<String, ?>> data 一个Map所组成的List集合
         *        每一个Map都会去对应ListView列表中的一行
         *        每一个Map中的键都必须包含所有在from中所指定的键
         * resource ：列表项布局文件的ID
         * from : Map中的键名
         * to : 绑定数据视图中的ID,与from成对应关系
         */
        //2.适配器加载数据源
        dataList = new ArrayList<Map<String, Object>>();
        simpleAdapter = new SimpleAdapter(this,getData(),R.layout.item,new String[]{"pic","text"},new int[]{R.id.pic,R.id.text});
        //3.视图（ListView）加载适配器（simpleAdapter）
        listView.setAdapter(simpleAdapter);


    }

    private List<Map<String,Object>> getData() {
        for (int i = 0; i < 20; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("pic",R.drawable.wolf);
            map.put("text","狼人"+i);
            dataList.add(map);
        }
        return dataList;
    }

    public void buttonAllClick(View view) {
       GetBoatInfo();
    }

    //打开设置页面，可起到提醒服务
    public void ServiceClick(View view){

        Intent intent = new Intent(this, SetupActivity.class);//显示intent
        //intent.putExtra(LOGONNAME_MESSAGE, LogonName);
        startActivity(intent);

    }

    //根据userid获取登录用户名字
    public String GetUserName(String user_id){
        String Result="";
        try{
            String url = "http://218.242.28.98:29583/wl/GetUserNameFromUserID.php";
            Map<String, String> params = new HashMap<String, String>();
            params.put("USER_ID", user_id);
            String strResult = HttpUtils.submitPostData(url, params, "utf-8");
            JSONArray jsonArray = new JSONArray(strResult);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                 Result = jsonObject.getString("NAME");
            }
        }
        catch (Exception e) {

        }
        return Result;
    }

    //获取船舶名称等基本信息
    public void GetBoatInfo(){
        String url = "http://218.242.28.98:29583/wl/GetCarInfoJsonChuan.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("USER_ID", USER_ID);
        String strResult = HttpUtils.submitPostData(url, params, "utf-8");
        //
        try {
            listView = (ListView) findViewById(R.id.list);
            List<String> list = new ArrayList<String>();

            JSONArray jsonArray = new JSONArray(strResult);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sEV_ID = jsonObject.getString("EV_ID");
                String sEV_NAME = jsonObject.getString("EV_NAME");
                String sEV_NUM = jsonObject.getString("EV_NUM");
                String sONLINE = jsonObject.getString("ONLINE");

                if (sONLINE.equalsIgnoreCase("0")) {
                    sONLINE = "离线";
                } else if (sONLINE.equalsIgnoreCase("1")) {
                    sONLINE = "在线";
                }
                list.add(sEV_NAME + "/" + sEV_NUM + "/" + sONLINE);
                HXEV_ID.put(sEV_NAME, sEV_ID);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            //建立list点击的监听事件
            listView.setOnItemClickListener(this);
            listView.setOnScrollListener(this);

        } catch (Exception e) {

        }

    }

    public void buttonListClick(View view) {

        if (HXEV_ID.containsKey("瑞华一号")){
           String xStr=  HXEV_ID.get("瑞华一号");
           TextView tv= (TextView) findViewById(R.id.textView);
            tv.setText(xStr);
        }

        //以下列表实例
//        final TextView textView = (TextView)findViewById(R.id.text);
//        ListView listView=(ListView)findViewById(R.id.list);
//        List<String> list = new ArrayList<String>();
//        list.add("测试内容测试内容测试内容测试内容测试内容测试内容");
//        list.add("测试内容测试内容测试内容测试内容测试内容测试内容");
//        list.add("测试内容测试内容测试内容测试内容测试内容测试内容");
//        list.add("测试内容测试内容测试内容测试内容测试内容测试内容");
//        list.add("测试内容测试内容测试内容测试内容测试内容测试内容");
//        list.add("测试内容测试内容测试");
//
//        ///可以一直添加，在真机运行后可以下拉列表
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
//        listView.setAdapter(adapter);

    }
}

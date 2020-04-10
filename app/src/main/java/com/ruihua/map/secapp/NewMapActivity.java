package com.ruihua.map.secapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMapActivity extends AppCompatActivity  {

    public LocationClient mLocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private Context context;

    private EditText latitude,longitude;
    private double x ,y;
    private LatLng point;
    private BaiduMap map;
    private String BoatName="瑞华一号";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;// 保障context能保存获取临时数据正常成功
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_new_map);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //获取网页传递的参数
        Intent intent = getIntent();
        BoatName=intent.getStringExtra(EmptyActivity.BOATNAME_MESSAGE);
        TextView text = (TextView) findViewById(R.id.viewMessage);
        text.setText(BoatName);

        mapView = (MapView) findViewById(R.id.bmapView2);
        baiduMap = mapView.getMap();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub
                String sTitle= arg0.getTitle();
                Toast.makeText(getApplicationContext(), "Title:"+sTitle, Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //定位某个指定的地点
//        point = new LatLng(31.226084,121.362178);  //设定中心点坐标  瑞华总部
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
//        OverlayOptions options = new MarkerOptions().icon(icon).position(point);
//        baiduMap.addOverlay(options);
//        //定义地图状态
//        MapStatus mMapStatus = new MapStatus.Builder()
//                .target(point)
//                .zoom(12)
//                .build();
//        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        baiduMap.setMapStatus(mMapStatusUpdate);

        //下面为获取本机坐标位置
//        baiduMap.setMyLocationEnabled(true);
//        positionText = (TextView) findViewById(R.id.viewMessage);
//        List<String> permissionList = new ArrayList<>();
//        //判断是否授权
//        if (ContextCompat.checkSelfPermission(NewMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(NewMapActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//        if (ContextCompat.checkSelfPermission(NewMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!permissionList.isEmpty()) {
//            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(NewMapActivity.this, permissions, 1);
//        } else {
//            requestLocation();
//        }

        SetPoisiton();
    }

    public void DWClick(View view) {
        SetPoisiton();
    }

    //定位
    public void SetPoisiton(){

        String latlog = GetLatLng();
        String strX = latlog.substring(0, latlog.indexOf("/"));
        String strY = latlog.substring(latlog.indexOf("/") + 1, latlog.length());

        x = Double.parseDouble(strX.trim());
        y = Double.parseDouble(strY.trim());

        point = new LatLng(x, y);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        OverlayOptions options = new MarkerOptions().icon(icon).position(point);

        String tempstr =BoatName+"-"+ Double.toString(x) + "-" + Double.toString(y);
        ((MarkerOptions) options).title(tempstr);//add title
        baiduMap.addOverlay(options);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(12)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);

    }
    //获取经纬度
    public String GetLatLng(){
        String result="";
        Map<String, String> params = new HashMap<String, String>();
        params.put("EV_NAME", BoatName);
        String strUrlPath = "http://218.242.28.98:29583/wl/GetCarInfoFromName.php";
        String all = "";
        String strCarInfo = HttpUtils.submitPostData(strUrlPath, params,"utf-8");

        try {
            JSONArray jsonArray = new JSONArray(strCarInfo);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Double sGPS_LAT = jsonObject.getDouble("GPS_LAT");
                Double sGPS_LNG = jsonObject.getDouble("GPS_LNG");

                all =  sGPS_LAT + "/" + sGPS_LNG;
            }
            result=all;

        } catch (Exception e) {

        }
        return  result;
    }

    public void MarkClick(View view) {
        //
        Intent intent = new Intent(this, EmptyActivity.class);
        startActivity(intent);
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(location.getCountry()).append("\n");
//            currentPosition.append("省：").append(location.getProvince()).append("\n");
//            currentPosition.append("市：").append(location.getCity()).append("\n");
//            currentPosition.append("区：").append(location.getDistrict()).append("\n");
//            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            //positionText.setText(currentPosition);
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }

    }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });




}

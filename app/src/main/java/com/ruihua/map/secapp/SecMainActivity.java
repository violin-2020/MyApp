package com.ruihua.map.secapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SecMainActivity extends AppCompatActivity {
    /**
     * 定位SDK核心类
     */
    private LocationClient locationClient;
    /**
     * 定位监听
     */
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * 百度地图控件
     */
    private MapView mapView;
    /**
     * 百度地图对象
     */
    private BaiduMap baiduMap;

    boolean isFirstLoc = true; // 是否首次定位
    List<LatLng> points = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //隐藏当前页标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sec_main);
        //
        //获取百度地图控件
        mapView = (MapView) findViewById(R.id.bmapView);
        //获取百度地图对象
        baiduMap = mapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        /**
         * 定位初始化
         */
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注冊监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
    }

    private Marker routeMarker;
    private int ROUTETIME = 300;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 100){
                LatLng p_baidu = (LatLng)msg.obj;
                if(points.size() < 2){ points.add(p_baidu); points.add(p_baidu); }
                else{ points.add(p_baidu); }
                OverlayOptions overlayOptions = new PolylineOptions() .width(20) .color(0X7FFF0000) .points(points);
                baiduMap.addOverlay(overlayOptions);
                //添加标记

                LatLng p0 = new LatLng(39.917038, 116.392339);//这里坐标可以根据定位或者其他数据来源

                // marker 的图片

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.photo);
                //marker option
                MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(p0);

                baiduMap.addOverlay(markerOptions);
                //marker 点击 这里是显示了距离 当然也可以自定义其他
                baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    @Override

                    public boolean onMarkerClick(Marker marker) {


                        LatLng p = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                       // String str = getDistance(baiduPoint_now,p);
                        String str = "123";

                        Toast.makeText(SecMainActivity.this,"该地点与您现在的直线距离是"+str,Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });

                //marker 移除

                Marker marker = (Marker) baiduMap.addOverlay(markerOptions);

                marker.remove();

            }
        };
    };


    public void PoClick(View view) {

        Toast.makeText(SecMainActivity.this, "test", 1).show();

//        var map = new BMap.Map("container");
//        var point = new BMap.Point(116.404, 39.915);
//        map.centerAndZoom(point, 15);
//        var marker = new BMap.Marker(point);        // 创建标注
//        map.addOverlay(marker);

//        LatLng p0 = new LatLng(39.917038, 116.392339);//这里坐标可以根据定位或者其他数据来源
//        // marker 的图片
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.photo);
//        //marker option
//        MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(p0);
//
//        baiduMap.addOverlay(markerOptions);


        try
        {
           // startActivity(new Intent("com.ruihua.map.secapp.NewMapActivity"));
            Intent intent = new Intent(this, NewMapActivity.class);//显示intent
            startActivity(intent);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发人员获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}

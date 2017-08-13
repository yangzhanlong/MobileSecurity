package org.me.mobilesecurity.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.GPSUtils;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class GPSService extends Service {

    private LocationManager mLocationManager;
    private GPSListener mGpsListener;

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkPermission();
        // 获得位置管理者
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 注册位置监听
        mGpsListener = new GPSListener();

        // 本地信息更新
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkPermission();
        // 注销位置监听
        mLocationManager.removeUpdates(mGpsListener);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    // 实现listener
    private class GPSListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.v("GPSService", "onLocationChanged");

            // 获取经纬度
            double latitude = location.getLatitude();// 纬度
            double longitude = location.getLongitude();// 经度

            // 转换为正确的经纬度
            double[] result = GPSUtils.parse(GPSService.this, latitude,
                    longitude);

            // 将转换后的经纬度发送给安全号码
            SmsManager smsManager = SmsManager.getDefault();

            // 获取安全号码
            String number = PreferenceUtils.getString(GPSService.this, Config.KEY_SJFD_NUM);
            String text = "longitude:" + result[0] + "  latitude:" + result[1];

            // 发送短信
            smsManager.sendTextMessage(number, null, text, null, null);

            // 停止服务
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

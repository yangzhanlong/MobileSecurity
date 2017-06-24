package org.me.mobilesecurity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.PackageUtils;
import org.me.mobilesecurity.utils.StreamUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private TextView mTvVersion;
    private HttpURLConnection conn;
    private static final int WHAT_SHOW_UPDATE_DIALOG = 100;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case WHAT_SHOW_UPDATE_DIALOG:
                    showSafeUpdateDialog(bundle.getString("content"),
                            bundle.getFloat("versionName"));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
        mTvVersion.setText(getString(R.string.version_name) + PackageUtils.getVersionName(this));

        checkVersionUpdate();
    }

    private void checkVersionUpdate(){
        // 版本更新检测
        // 去网络中获得最新的版本信息
        new Thread(new CheckVersionTask()).start();
    }

    private void loadToHome(){
        // 延时操作
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 此方法是执行在主线程中的
                Log.v("SplashActivity", Thread.currentThread().getName());

                // 结束当前页面，进入主页面
                finish();
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }

    /**
     * 检测版本更新的任务
     *
     * @author Administrator
     *
     */
    private class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            try {
                // 去网络中获得最新的版本信息
                // **** 从网络中需要获得versionCode
                URL url = new URL("http://192.168.80.53:8080/info.json");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2 * 1000); // 设置网络连接的超时时间
                conn.setReadTimeout(2 * 1000); // 设置网络响应的超时时间
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 读取网络的响应流
                    InputStream inputStream = conn.getInputStream();

                    // 解析网络流，获得需要的信息
                    String json = StreamUtils.parseStream(inputStream);
                    Log.v("SplashActivity", json);

                    // 解析json
                    JSONObject object = new JSONObject(json);
                    int netVersionCode = object.getInt("versionCode");
                    Log.v("SplashActivity", "netVersionCode:" + netVersionCode);

                    int localVersionCode = PackageUtils.getVersionCode(SplashActivity.this);
                    Log.v("SplashActivity", "localVersionCode:" + localVersionCode);

                    String content = object.getString("content");
                    Log.v("SplashActivity", "content:" + content);

                    Float netVersionName = BigDecimal.valueOf(object.getDouble("versionName")).floatValue();

                    // 2. 根据网络版本和本地版本进行比对，判断是否需要更新
                    if (netVersionCode > localVersionCode) {
                        // 提醒用户更新
                        showUpdateDialog(content, netVersionName);
                    } else {
                        // 结束当前页面，进入主页面
                        loadToHome();
                    }
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        }
    }

    private void showUpdateDialog(String content, Float netVersionName) {
        // 需要在主线程中执行
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        msg.what = WHAT_SHOW_UPDATE_DIALOG;
        bundle.putString("content", content);
        bundle.putFloat("versionName", netVersionName);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void showSafeUpdateDialog(String content, Float versionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置title
        builder.setTitle(getString(R.string.check_new_version) + versionName);
        // 设置message,由服务器指定的
        builder.setMessage(content);
        // 点击其他区域不可取消dialog
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.next_time_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}

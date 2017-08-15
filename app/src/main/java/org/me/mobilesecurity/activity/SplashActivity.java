package org.me.mobilesecurity.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.me.mobilesecurity.R;
import org.me.mobilesecurity.services.ProtectingService;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.GZipUtils;
import org.me.mobilesecurity.utils.PackageUtils;
import org.me.mobilesecurity.utils.PreferenceUtils;
import org.me.mobilesecurity.utils.ServiceStateUtils;
import org.me.mobilesecurity.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private TextView mTvVersion;
    private String mDownloadUrl;
    private HttpURLConnection conn;
    private static final int REQUEST_CODE = 1;
    private static final int WHAT_SHOW_UPDATE_DIALOG = 100;
    private static final int WHAT_SHOW_ERROR = 102;
    private static final int REQUEST_CODE_INSTALL = 101;
    private ProgressDialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case WHAT_SHOW_UPDATE_DIALOG:
                    showSafeUpdateDialog(bundle.getString("content"),
                            bundle.getFloat("versionName"));
                    break;
                case WHAT_SHOW_ERROR:
                    Toast.makeText(SplashActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    loadToHome();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
        mTvVersion.setText(getString(R.string.version_name) + PackageUtils.getVersionName(this));

        // 如果设置了自动更新
        boolean flag = PreferenceUtils.getBoolean(this, Config.KEY_AUTO_UPDATE,
                true);
        if (flag) {
            // 版本更新检测
            checkVersionUpdate();
        } else {
            loadToHome();
        }

        // 加载归属地数据库
        copyNumberAddressDB();
        
        // 加载常用号码数据库
        copyCommonNumberDB();

        // 开启服务
        if (!ServiceStateUtils.isServiceRunning(this, ProtectingService.class)) {
            startService(new Intent(this, ProtectingService.class));
        }
    }


    /**
     * 拷贝归属地数据库
     */
    private void copyNumberAddressDB() {
        File file = new File(getFilesDir(), "address.db");
        if (file.exists()) {
            return;
        }

        AssetManager assets = getAssets();
        try {
            InputStream in = assets.open("address.zip");
            FileOutputStream fso = new FileOutputStream(file);

            // 解压
            GZipUtils.unzip(in, fso);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝常用号码数据库
     */
    private void copyCommonNumberDB() {
        File file = new File(getFilesDir(), "commonnum.db");
        if (file.exists()) {
            return;
        }

        InputStream in = null;
        FileOutputStream fso = null;
        AssetManager assets = getAssets();
        try {
            in = assets.open("commonnum.db");
            fso = new FileOutputStream(file);

            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1 ) {
                fso.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(in);
            StreamUtils.closeIO(fso);
        }
    }

    private void checkVersionUpdate() {
        // 版本更新检测
        // 去网络中获得最新的版本信息
        new Thread(new CheckVersionTask()).start();
    }

    private void loadToHome() {
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
        }, 1500);
    }

    /**
     * 检测版本更新的任务
     *
     * @author Administrator
     */
    private class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            try {
                // 去网络中获得最新的版本信息
                // **** 从网络中需要获得versionCode
                URL url = new URL("http://192.168.56.1:8080/info.json");
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

                    mDownloadUrl = object.getString("url");

                    // 根据网络版本和本地版本进行比对，判断是否需要更新
                    if (netVersionCode > localVersionCode) {
                        // 提醒用户更新
                        showUpdateDialog(content, netVersionName);
                    } else {
                        // 结束当前页面，进入主页面
                        loadToHome();
                    }
                }

            } catch (java.io.IOException e) {
                showError(getString(R.string.error_101));
            } catch (JSONException e) {
                showError(getString(R.string.error_102));
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
                downloadNewApk();
            }
        });
        builder.setNegativeButton(R.string.next_time_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadToHome();
            }
        });

        builder.show();
    }

    private void downloadNewApk() {
        // 弹出进度的dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);

        // 注册权限
        if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
            } else {
                // 申请授权。
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            // 去网络下载apk
            dialog.show();
            new Thread(new DownloadApkTask(dialog)).start();
        }
    }

    private class DownloadApkTask implements Runnable {
        private ProgressDialog mDialog;

        public DownloadApkTask(ProgressDialog dialog) {
            this.mDialog = dialog;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            InputStream inputStream = null;
            String url = mDownloadUrl;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(5 * 1000);

                inputStream = conn.getInputStream();

                // 获得要下载文件的大小
                int contentLength = conn.getContentLength();
                dialog.setMax(contentLength);

                // 指定输出的apk文件,sdcard下
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath(),
                        System.currentTimeMillis() + ".apk");

                // 写到文件中
                fos = new FileOutputStream(file);

                int len = -1;
                int progress = 0;
                byte[] buffer = new byte[1024];

                // 反复的读写输入流
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    progress += len;
                    // 设置进度
                    dialog.setProgress(progress);
                    Thread.sleep(5);
                }
                dialog.dismiss();

                // 提示安装
                installApk(file);

            } catch (IOException e) {
                showError(getString(R.string.error_103));
            } catch (InterruptedException e) {
                showError(getString(R.string.error_104));
            } finally {
                StreamUtils.closeIO(fos);
                StreamUtils.closeIO(inputStream);
            }
        }
    }

    private void showError(String error) {
        Message msg = Message.obtain();
        msg.what = WHAT_SHOW_ERROR;
        msg.obj = error;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog.show();
                    new Thread(new DownloadApkTask(dialog)).start();
                } else {
                    Toast.makeText(this, R.string.deny_perm, Toast.LENGTH_SHORT).show();
                    loadToHome();
                }
                return;
        }
    }

    private void installApk(File file) {
        // 发送隐式意图去安装apk
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        // 7.0使用FileProvider获取 apk uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            Uri apkUri = FileProvider.getUriForFile(this, "org.me.mobilesecurity.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }

        startActivityForResult(intent, REQUEST_CODE_INSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INSTALL) {
            // 响应的是安装的请求结果
            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                loadToHome();
            }
        }
    }
}

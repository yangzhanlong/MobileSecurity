package org.me.mobilesecurity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.CheckBox;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SetupActivity5 extends BaseSetUpActivity {

    private static final int REQUEST_CODE = 100;
    private CheckBox mCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);

        initView();
    }

    private void initView() {
        mCheckBox = (CheckBox) findViewById(R.id.setup5_cb_protecting);
        boolean check = PreferenceUtils.getBoolean(this, Config.KEY_SJFD_PROTECT);
        mCheckBox.setChecked(check);
    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity4.class);
        startActivity(intent);
        return false;
    }

    // 跳转到设置完成界面
    @Override
    protected boolean performNext() {
        // 判断是否勾选
        if (!mCheckBox.isChecked()) {
            Toast.makeText(this, "勾选后才可以开启防盗保护", Toast.LENGTH_SHORT).show();
            return true;
        }

        // 存储勾选状态
        PreferenceUtils.setBoolean(this, Config.KEY_SJFD_PROTECT, true);

        // 存储设置过设置向导
        PreferenceUtils.setBoolean(this, Config.KEY_SJFD_SETUP, true);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SetupActivity5.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, SjfdActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, SjfdActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.deny_perm, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}

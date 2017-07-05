package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SetupActivity5 extends BaseSetUpActivity {

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

        Intent intent = new Intent(this, SjfdActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}

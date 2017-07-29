package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.services.CallSmsSafeService;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;
import org.me.mobilesecurity.utils.ServiceStateUtils;
import org.me.mobilesecurity.view.SettingItemView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private SettingItemView mSivAutoUpdate;
    private SettingItemView mSivCallSmsSafe;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        initEvent();
    }

    private void initView() {
        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
        mSivCallSmsSafe = (SettingItemView) findViewById(R.id.mSivCallSmsSafe);

        // 校验自动更新的状态
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(
                SettingActivity.this, Config.KEY_AUTO_UPDATE, true));
    }

    private void initEvent() {
        mSivAutoUpdate.setOnClickListener(this);
        mSivCallSmsSafe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_siv_autoupdate:
                clickAutoUpdate();
                break;
            case R.id.mSivCallSmsSafe:
                clickCallSmsSafe();
                break;
        }
    }

    /**
     * 点击自动更新的开关操作
     */
    private void clickAutoUpdate() {
        // 获取已经保存的数据
        boolean flag = PreferenceUtils.getBoolean(SettingActivity.this,
                Config.KEY_AUTO_UPDATE, true);
        // 如果已经设置了自动更新，点击时，要关闭,否则相反
        mSivAutoUpdate.setToggleState(!flag);
        PreferenceUtils.setBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, !flag);
    }

    /**
     * 骚扰拦截的开关设置
     */
    private void clickCallSmsSafe() {
        // 如果服务开启，就关闭，否则相反
        if (ServiceStateUtils.isServiceRunning(this, CallSmsSafeService.class)) {
            Intent intent = new Intent(this, CallSmsSafeService.class);
            stopService(intent);
            mSivCallSmsSafe.setToggleState(false);
        } else {
            Intent intent = new Intent(this, CallSmsSafeService.class);
            startService(intent);
            mSivCallSmsSafe.setToggleState(true);
        }
    }
}

package org.me.mobilesecurity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;
import org.me.mobilesecurity.view.SettingItemView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private SettingItemView mSivAutoUpdate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        initEvent();
    }

    private void initView() {
        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);

        // 校验自动更新的状态
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(
                SettingActivity.this, Config.KEY_AUTO_UPDATE, true));
    }

    private void initEvent() {
        mSivAutoUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_siv_autoupdate:
                clickAutoUpdate();
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
}

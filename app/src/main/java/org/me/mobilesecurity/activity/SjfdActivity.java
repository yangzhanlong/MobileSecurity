package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SjfdActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTvNumber;
    private ImageView mIvProtecting;
    private RelativeLayout mRlSetup;
    private RelativeLayout mRlProtecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd);

        initView();
        initEvent();
    }


    private void initView() {
        // 设置安全号码
        mTvNumber = (TextView) findViewById(R.id.sjfd_tv_number);
        String number = PreferenceUtils.getString(this, Config.KEY_SJFD_NUM);
        mTvNumber.setText(number);

        // 设置保护锁
        mIvProtecting = (ImageView) findViewById(R.id.sjfd_iv_protecting);
        boolean protect = PreferenceUtils.getBoolean(this, Config.KEY_SJFD_PROTECT);
        mIvProtecting.setImageResource(protect ? R.mipmap.lock : R.mipmap.unlock);

        mRlSetup = (RelativeLayout) findViewById(R.id.sjfd_rl_setup);
        mRlProtecting = (RelativeLayout) findViewById(R.id.sjfd_rl_protecting);
    }


    private void initEvent() {
        mRlSetup.setOnClickListener(this);
        mRlProtecting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sjfd_rl_setup:
                clickSetup();
                break;
            case R.id.sjfd_rl_protecting:
                clickProtecting();
                break;
        }
    }

    private void clickSetup() {
        Intent intent = new Intent(this, SetupActivity1.class);
        startActivity(intent);
        finish();
    }

    private void clickProtecting(){
        // 如果当前是已经开启防盗保护，点击时就取消保护，否则相反
        boolean protect = PreferenceUtils.getBoolean(this, Config.KEY_SJFD_PROTECT);
        if (protect) {
            mIvProtecting.setImageResource(R.mipmap.unlock);
            PreferenceUtils.setBoolean(this, Config.KEY_SJFD_PROTECT, false);
        } else {
            mIvProtecting.setImageResource(R.mipmap.lock);
            PreferenceUtils.setBoolean(this, Config.KEY_SJFD_PROTECT, true);
        }
    }
}

package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.view.SettingItemView;

public class CommonToolActivity extends AppCompatActivity implements View.OnClickListener{
    private SettingItemView mSivNumberAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);

        initView();
        initEvent();
    }

    private void initView() {
        mSivNumberAddress = (SettingItemView) findViewById(R.id.ct_siv_number_address);
    }

    private void initEvent() {
        mSivNumberAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ct_siv_number_address:
                clickNumberAddress();
                break;
            default:
                break;
        }

    }

    private void clickNumberAddress() {
        Intent intent = new Intent(this, NumberAddressActivity.class);
        startActivity(intent);
    }
}

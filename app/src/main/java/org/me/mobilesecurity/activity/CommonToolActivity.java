package org.me.mobilesecurity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.business.SmsProvider;
import org.me.mobilesecurity.view.SettingItemView;

public class CommonToolActivity extends AppCompatActivity implements View.OnClickListener{
    private SettingItemView mSivNumberAddress;
    private SettingItemView mSivCommonNumber;

    private SettingItemView mSivSmsBackup;
    private SettingItemView mSivSmsRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);

        initView();
        initEvent();
    }

    private void initView() {
        mSivNumberAddress = (SettingItemView) findViewById(R.id.ct_siv_number_address);
        mSivCommonNumber = (SettingItemView) findViewById(R.id.ct_siv_common_number);
        mSivSmsBackup = (SettingItemView) findViewById(R.id.ct_siv_sms_backup);
        mSivSmsRestore = (SettingItemView) findViewById(R.id.ct_siv_sms_restore);
    }

    private void initEvent() {
        mSivNumberAddress.setOnClickListener(this);
        mSivCommonNumber.setOnClickListener(this);
        mSivSmsBackup.setOnClickListener(this);
        mSivSmsRestore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ct_siv_number_address:
                clickNumberAddress();
                break;
            case R.id.ct_siv_common_number:
                clickCommonNumber();
                break;
            case R.id.ct_siv_sms_backup:
                clickSmsBackup();
                break;
            case R.id.ct_siv_sms_restore:
                clickSmsRestore();
                break;
            default:
                break;
        }

    }

    private void clickCommonNumber() {
        Intent intent = new Intent(this, CommonNumberActivity.class);
        startActivity(intent);
    }

    private void clickNumberAddress() {
        Intent intent = new Intent(this, NumberAddressActivity.class);
        startActivity(intent);
    }

    private void clickSmsBackup() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // final TextView tv = (TextView) findViewById(R.id.ct_siv_tv_progress);

        // 所有的接口只和UI相关
        SmsProvider.smsBackup(this, new SmsProvider.OnSmsListener() {
            @Override
            public void onPre() {
                dialog.show();
                // tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(int max, int progress) {
                dialog.setMax(max);
                dialog.setProgress(progress);
                // tv.setText(progress + "/" + max);
            }

            @Override
            public void onFinish(boolean sucess) {
                // tv.setVisibility(View.GONE);
                dialog.dismiss();
                if (sucess) {
                    Toast.makeText(CommonToolActivity.this, "备份成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommonToolActivity.this, "备份失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void clickSmsRestore() {
        // 读取短信备份的文件---》对象---》插入数据 --->UI显示

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        SmsProvider.smsRestore(this, new SmsProvider.OnSmsListener() {

            @Override
            public void onPre() {
                dialog.show();
            }

            @Override
            public void onProgress(int max, int progress) {
                dialog.setMax(max);
                dialog.setProgress(progress);
            }

            @Override
            public void onFinish(boolean sucess) {
                dialog.dismiss();
                if (sucess) {
                    Toast.makeText(CommonToolActivity.this, "还原成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommonToolActivity.this, "还原失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

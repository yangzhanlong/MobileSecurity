package org.me.mobilesecurity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SetupActivity2 extends BaseSetUpActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 100;
    private RelativeLayout mRlBind;
    private ImageView mIvBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initView();
        initEvent();
    }
    
//    // 下一步
//    public void clickNext(View view) {
//        Intent intent = new Intent(this, SetupActivity3.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
//    }
//
//    // 上一步
//    public void clickBack(View view) {
//        Intent intent = new Intent(this, SetupActivity1.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
//    }

    private void initView() {
        mRlBind = (RelativeLayout) findViewById(R.id.setup2_rl_bind);
        mIvBind = (ImageView) findViewById(R.id.setup2_iv_bind);

        String sim = PreferenceUtils.getString(this, Config.KEY_SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            mIvBind.setImageResource(R.mipmap.unlock);
        } else {
            mIvBind.setImageResource(R.mipmap.lock);
        }
    }


    private void initEvent() {
        mRlBind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setup2_rl_bind:
                clickBind();
        }
    }

    private void clickBind() {
        String sim = PreferenceUtils.getString(this, Config.KEY_SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            // 如果没有绑定sim，绑定
            bindSim();
        } else {
            // 如果已经绑定sim，解绑
            PreferenceUtils.setString(this, Config.KEY_SJFD_SIM, null);
            mIvBind.setImageResource(R.mipmap.unlock);
        }
    }

    private void bindSim() {
        // 注册权限
        if (ContextCompat.checkSelfPermission(SetupActivity2.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(SetupActivity2.this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                // 申请授权。
                ActivityCompat.requestPermissions(SetupActivity2.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE,}, REQUEST_CODE);
            }
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String sim = tm.getSimSerialNumber();
            PreferenceUtils.setString(this, Config.KEY_SJFD_SIM, sim);
            mIvBind.setImageResource(R.mipmap.lock);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String sim = tm.getSimSerialNumber();
                    PreferenceUtils.setString(this, Config.KEY_SJFD_SIM, sim);
                    mIvBind.setImageResource(R.mipmap.lock);
                } else {
                    Toast.makeText(this, R.string.deny_perm, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity1.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        String sim = PreferenceUtils.getString(this, Config.KEY_SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "如果要开启防盗保护,必须绑定SIM卡", Toast.LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent(this, SetupActivity3.class);
        startActivity(intent);
        return false;
    }
}

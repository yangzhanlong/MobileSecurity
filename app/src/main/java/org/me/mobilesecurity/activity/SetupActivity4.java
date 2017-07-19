package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.receiver.SjfdAdminReceiver;

public class SetupActivity4 extends BaseSetUpActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_ENABLE_ADMIN = 100;
    private RelativeLayout mRl;
    private ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initView();
        initEvent();
    }

    private void initView() {
        mRl = (RelativeLayout) findViewById(R.id.setup4_rl_admin);
        mIv = (ImageView) findViewById(R.id.setup4_iv_admin);

        // 初始化时，显示正确的图片状态
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, SjfdAdminReceiver.class);
        mIv.setImageResource(dpm.isAdminActive(who) ? R.mipmap.admin_activated : R.mipmap.admin_inactivated);
    }

    private void initEvent() {
        mRl.setOnClickListener(this);
    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity3.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        // 判断是否激活了设备管理员
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        ComponentName who = new ComponentName(this, SjfdAdminReceiver.class);
        if (!dpm.isAdminActive(who)) {
            Toast.makeText(this, "如果要开启防盗保护,必须激活设备管理员", Toast.LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent(this, SetupActivity5.class);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setup4_rl_admin:
                clickAdmin();
                break;
        }
    }

    // 激活设备管理员
    private void clickAdmin() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, SjfdAdminReceiver.class);

        if (!dpm.isAdminActive(who)) {
            // 没有激活 则去打开激活界面
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.add_admin_extra_app_text));
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        } else {
            // 已经激活，注销激活
            dpm.removeActiveAdmin(who);
            mIv.setImageResource(R.mipmap.admin_inactivated);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                mIv.setImageResource(R.mipmap.admin_activated);
            }
        }
    }
}

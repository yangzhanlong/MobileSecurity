package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

public class SetupActivity3 extends BaseSetUpActivity {

    private static final int REQUEST_NUMBER = 100;
    private EditText et_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initView();
    }

    private void initView() {
        et_number = (EditText) findViewById(R.id.setup3_et_number);

        // 回显号码
        String number = PreferenceUtils.getString(this, Config.KEY_SJFD_NUM);
        et_number.setText(number);
        // 使光标到号码末尾
        if (!TextUtils.isEmpty(number)) {
            et_number.setSelection(number.length());
        }
    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity2.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        String number = et_number.getText().toString().trim();
        // 如果没有设置号码，中断下一步
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "如果要开启防盗保护,必须设置安全号码", Toast.LENGTH_SHORT).show();
            return true;
        }

        // 保存安全号码
        PreferenceUtils.setString(this, Config.KEY_SJFD_NUM, number);

        Intent intent = new Intent(this, SetupActivity4.class);
        startActivity(intent);
        return false;
    }

    public void clickContact(View view) {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        startActivityForResult(intent, REQUEST_NUMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NUMBER && resultCode == Activity.RESULT_OK) {
            String number = data.getStringExtra("number");
            et_number.setText(number);
            // 使光标到号码末尾
            if (!TextUtils.isEmpty(number)) {
                et_number.setSelection(number.length());
            }
        }
    }
}

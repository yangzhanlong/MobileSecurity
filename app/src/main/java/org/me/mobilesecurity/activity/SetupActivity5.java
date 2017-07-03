package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;

import org.me.mobilesecurity.R;

public class SetupActivity5 extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);
    }

//    // 跳转到设置完成界面
//    public void clickNext(View view) {
//        Intent intent = new Intent(this, SjfdActivity.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
//    }
//
//    public void clickBack(View view) {
//        Intent intent = new Intent(this, SetupActivity4.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
//    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity4.class);
        startActivity(intent);
        return false;
    }

    // 跳转到设置完成界面
    @Override
    protected boolean performNext() {
        Intent intent = new Intent(this, SjfdActivity.class);
        startActivity(intent);
        return true;
    }
}

package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;

import org.me.mobilesecurity.R;

public class SetupActivity1 extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

//    public void clickNext(View view) {
//        Intent intent = new Intent(this, SetupActivity2.class);
//        startActivity(intent);
//        finish();
//
//        // 设置过渡动画
//        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
//    }

    @Override
    protected boolean performBack() {
        return true;
    }

    @Override
    protected boolean performNext() {
        Intent intent = new Intent(this, SetupActivity2.class);
        startActivity(intent);
        return false;
    }
}

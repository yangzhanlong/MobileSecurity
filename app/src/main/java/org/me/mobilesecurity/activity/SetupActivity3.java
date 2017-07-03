package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.me.mobilesecurity.R;

public class SetupActivity3 extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

//    public void clickNext(View view) {
//        Intent intent = new Intent(this, SetupActivity4.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
//    }
//
//    public void clickBack(View view) {
//        Intent intent = new Intent(this, SetupActivity2.class);
//        startActivity(intent);
//        finish();
//        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
//    }

    @Override
    protected boolean performBack() {
        Intent intent = new Intent(this, SetupActivity2.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        Intent intent = new Intent(this, SetupActivity4.class);
        startActivity(intent);
        return false;
    }

    public void clickContact(View view) {

    }
}

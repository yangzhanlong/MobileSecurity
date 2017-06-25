package org.me.mobilesecurity.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.me.mobilesecurity.R;

public class HomeActivity extends AppCompatActivity {

    private ImageView ivLogon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 初始化控件
        initView();

        // 让logo进行动画
        doLogoAnimation();
    }

    private void initView(){
        ivLogon = (ImageView) findViewById(R.id.home_iv_logo);
    }

    private void doLogoAnimation() {
        // 使用属性动画实现立体旋转
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivLogon, "RotationY", 0, 90, 180, 270, 360);
        animator.setDuration(3000);
        animator.setRepeatCount(ObjectAnimator.INFINITE); // 设置次数
        animator.setRepeatMode(ObjectAnimator.REVERSE); // 设置模式
        animator.start();
    }

    public void clickSetting(View view) {
    }
}

package org.me.mobilesecurity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.me.mobilesecurity.R;

public abstract class BaseSetUpActivity extends AppCompatActivity {

    private static final String TAG = "BaseSetUpActivity";
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建手势识别器
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            // 监听触摸滑动
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                // e1:手势开始时的点
                // e2:手势结束时的点
                // velocityX :水平方向的速率
                // velocityY :垂直方向的速率
                float x1 = e1.getRawX();
                float y1 = e1.getRawY();

                float x2 = e2.getRawX();
                float y2 = e2.getRawY();

                // 水平速率小于200不执行
                if (Math.abs(velocityX) < 200) {
                    Log.d(TAG, "水平速率: " + velocityX);
                    return true;
                }

                // 垂直方向的判断  Y轴运动不执行
                if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                    // Y轴运动
                    Log.d(TAG, "Y轴运动");
                    return true;
                }

                if (x1 > x2) {  // x1 > x2表示从右向左滑动，执行下一步的操作
                    Log.d(TAG, "手势执行下一步的操作");
                    doNext();
                } else {  // x1 < x2表示从左向右滑动，执行上一步的操作
                    Log.d(TAG, "手势执行上一步的操作");
                    doBack();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 手势识别器捕获分析touch行为
        return mGestureDetector.onTouchEvent(event);
    }

    public void clickNext(View view) {
        doNext();
    }

    public void clickBack(View view) {
       doBack();
    }

    private void doNext() {
        if (performNext()) {
            Log.v(TAG, "中断下一步操作");
            return;
        }
        // 设置过渡动画  p1:进入的activity对应的动画资源 p2:结束的activity对应的动画资源
        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
        finish();
    }

    private void doBack() {
        if (performBack()) {
            Log.v(TAG, "中断上一步操作");
            return;
        }
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        finish();
    }

    /**
     * 上一步的操作
     *
     * @return true:不往下执行了 false:继续往下执行
     */
    protected abstract boolean performBack();

    /**
     * 下一步的操作
     *
     * @return true:不往下执行了 false:继续往下执行
     */
    protected abstract boolean performNext();
}

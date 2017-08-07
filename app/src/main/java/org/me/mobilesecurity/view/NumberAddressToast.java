package org.me.mobilesecurity.view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

/**
 * 自定义的号码归属地显示Toast
 */
public class NumberAddressToast implements View.OnTouchListener{
    private WindowManager mWM;
    private View mView;
    private Context mContext;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    float mDownX;
    float mDownY;

    public NumberAddressToast(Context context) {
        this.mContext = context;
        // 初始化WindowManager
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 加载自定义Toast的样式
        mView = View.inflate(context, R.layout.toast_number_address, null);
        // param初始化
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; // 可以触摸
        params.setTitle("Toast");

        // 监听toast触摸事件
        mView.setOnTouchListener(this);
    }

    public void show(String address) {
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }

        // 设置样式
        int style = PreferenceUtils.getInt(mContext, Config.KEY_ADDRESS_STYLE, -1);
        if (style == -1) {
            style = R.drawable.toast_normal;
        }
        mView.setBackgroundResource(style);

        TextView tv = (TextView) mView.findViewById(R.id.toast_tv_location);
        tv.setText(address);

        mWM.addView(mView, mParams);
    }

    public void hide() {
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();

                float diffX = moveX - mDownX;
                float diffY = moveY - mDownY;

                mParams.x += diffX;
                mParams.y += diffY;

                // 更新toast位置
                mWM.updateViewLayout(mView, mParams);

                mDownX = moveX;
                mDownY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }


        return false;
    }
}

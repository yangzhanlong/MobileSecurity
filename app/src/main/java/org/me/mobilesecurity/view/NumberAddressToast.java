package org.me.mobilesecurity.view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.me.mobilesecurity.R;

/**
 * 自定义的号码归属地显示Toast
 */
public class NumberAddressToast {
    private WindowManager mWM;
    private View mView;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    public NumberAddressToast(Context context) {
        // 初始化WindowManager
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 加载自定义Toast的样式
        mView = View.inflate(context, R.layout.toast_number_address, null);
        // param初始化
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
    }

    public void show(String address) {
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }
        TextView tv = (TextView) mView.findViewById(R.id.toast_tv_location);
        tv.setText(address);

        mWM.addView(mView, mParams);
    }

    public void hide() {
        if (mView.getParent() != null) {
            mWM.removeView(mView);
        }
    }
}

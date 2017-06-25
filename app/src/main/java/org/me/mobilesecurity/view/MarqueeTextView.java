package org.me.mobilesecurity.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // android:singleLine="true"
        setSingleLine();

        // android:ellipsize="marquee"
        setEllipsize(TextUtils.TruncateAt.MARQUEE);

        // android:focusable="true"
        setFocusable(true);

        // android:focusableInTouchMode="true"
        setFocusableInTouchMode(true);

        // android:marqueeRepeatLimit="marquee_forever"
        setMarqueeRepeatLimit(-1);
    }

    /**
     * 判断控件初始化时是否有焦点
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            // 有焦点时走父类方法，焦点被抢走时，不去做操作
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            // 窗体有焦点时走父类的方法，窗体焦点消失，不去做操作
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }
}

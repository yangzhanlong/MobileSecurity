package org.me.mobilesecurity.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.me.mobilesecurity.R;

public class SettingItemView extends RelativeLayout {
    private final static int BACKGROUND_START = 0;
    private final static int BACKGROUND_MIDDLE = 1;
    private final static int BACKGROUND_END = 2;
    private TextView mTvText;
    private ImageView mIvToggle;

    public SettingItemView(Context context) {
        super(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载xml
        View.inflate(context, R.layout.view_setting_item, this);

        // 接收属性,自己赋值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        String text = ta.getString(R.styleable.SettingItemView_sivText);
        // 背景
        int background = ta.getInt(R.styleable.SettingItemView_sivBackground, BACKGROUND_START);
        boolean toggleEnable = ta.getBoolean(R.styleable.SettingItemView_sivToggleEnable, true);
        ta.recycle();

        mTvText = (TextView) findViewById(R.id.view_setting_tv_text);
        mIvToggle = (ImageView) findViewById(R.id.view_setting_iv_toggle);

        // 赋值
        mTvText.setText(text);
        switch (background) {
            case BACKGROUND_START:
                this.setBackgroundResource(R.drawable.item_first_selector);
                break;
            case BACKGROUND_MIDDLE:
                this.setBackgroundResource(R.drawable.item_middle_selector);
                break;
            case BACKGROUND_END:
                this.setBackgroundResource(R.drawable.item_last_selector);
                break;
        }

        // 开关图片
        mIvToggle.setVisibility(toggleEnable ? VISIBLE:GONE);
    }

    /**
     * 设置开关的状态
     *
     * @param open
     *            : true显示打开，false显示关闭
     */
    public void setToggleState(boolean open) {
        mIvToggle.setImageResource(open ? R.drawable.on : R.drawable.off);
    }
}

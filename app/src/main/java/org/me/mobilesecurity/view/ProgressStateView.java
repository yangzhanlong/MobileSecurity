package org.me.mobilesecurity.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.me.mobilesecurity.R;

public class ProgressStateView extends RelativeLayout{
    private TextView mTvText;
    private TextView mTvLeft;
    private TextView mTvRight;
    private ProgressBar mPbProgress;

    public ProgressStateView(Context context) {
        this(context, null);
    }

    public ProgressStateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载布局
        View.inflate(context, R.layout.view_progress_state, this);

        // 初始化view
        initView();
        // 设置属性值
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ProgressStateView);
        String text = ta.getString(R.styleable.ProgressStateView_psvText);
        ta.recycle();

        mTvText.setText(text);
    }

    private void initView() {
        mTvText = (TextView) findViewById(R.id.tv_title);
        mTvLeft = (TextView) findViewById(R.id.tv_left);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mPbProgress = (ProgressBar) findViewById(R.id.pb_progress);
    }

    /**设置左边文本*/
    public void setLeftText(String text) {
        mTvLeft.setText(text);
    }

    /**设置右边文本*/
    public void setRightText(String text) {
        mTvRight.setText(text);
    }

    /**设置当前的进度*/
    public void setProgress(int progress) {
        mPbProgress.setProgress(progress);
    }

    /**设置最大进度*/
    public void setMax(int max) {
        mPbProgress.setMax(max);
    }
}

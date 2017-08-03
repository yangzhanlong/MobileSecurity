package org.me.mobilesecurity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.db.NumberAddressDao;

public class NumberAddressActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEtNumber;
    private Button mBtnQuery;
    private TextView mTvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address);

        initView();
        initEvent();
    }


    private void initView() {
        mEtNumber = (EditText) findViewById(R.id.na_et_number);
        mBtnQuery = (Button) findViewById(R.id.na_btn_query);
        mTvAddress = (TextView) findViewById(R.id.na_tv_address);
    }

    private void initEvent() {
        mBtnQuery.setOnClickListener(this);

        // 号码动态查询
        mEtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // 文本内容改变时回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = mEtNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    return;
                }

                String address = NumberAddressDao.findAddress(NumberAddressActivity.this, number);
                mTvAddress.setText("归属地:" + address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.na_btn_query:
                clickQuery();
                break;
            default:
                break;
        }
    }

    private void clickQuery() {
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            // 抖动效果
            // Animation shake = AnimationUtils.loadAnimation(this,
            // R.anim.shake);
            // mEtNumber.startAnimation(shake);
            // android 所有的xml类型的文件其实会映射成java代码

            TranslateAnimation animation = new TranslateAnimation(0, 10, 0, 0);
            animation.setDuration(1000);
            // 设置抖动的次数
            animation.setInterpolator(new CycleInterpolator(10));
            mEtNumber.startAnimation(animation);

            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
        }
        String address = NumberAddressDao.findAddress(this, number);
        mTvAddress.setText("归属地:" + address);

    }
}

package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.BlackBean;
import org.me.mobilesecurity.db.BlackDao;

public class BlackEditActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String KEY_NUMBER = "number";
    public static final String KEY_TYPE = "type";

    private TextView mTvTitle;
    private EditText mEtNumber;
    private RadioGroup mRgType;
    private Button mBtnOk;
    private Button mBtnCancel;
    private BlackDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_edit);

        mDao = new BlackDao(this);

        initView();
        initEvent();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.be_tv_title);
        mEtNumber = (EditText) findViewById(R.id.be_et_number);
        mRgType = (RadioGroup) findViewById(R.id.be_rg_type);
        mBtnOk = (Button) findViewById(R.id.be_btn_ok);
        mBtnCancel = (Button) findViewById(R.id.be_btn_cancel);
    }

    private void initEvent() {
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.be_btn_ok:
                clickOk();
                break;
            case R.id.be_btn_cancel:
                clickCancel();
                break;
        }
    }

    private void clickOk() {
        // 检查号码是否有输入
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查radiogroup
        int checkedRadioButtonId = mRgType.getCheckedRadioButtonId(); // 获取选中的id
        if (checkedRadioButtonId == -1) { // 一个都没有选中
            Toast.makeText(this, "请选择拦截类型", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = -1; // call 0, sms 1, all 2
        switch (checkedRadioButtonId) {
            case R.id.be_rb_call:
                type = BlackBean.TYPE_CALL;
                break;
            case R.id.be_rb_sms:
                type = BlackBean.TYPE_SMS;
                break;
            case R.id.be_rb_all:
                type = BlackBean.TYPE_ALL;
                break;
            default:
                break;
        }

        boolean success = mDao.add(number, type);
        if (success) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra(KEY_NUMBER, number);
            data.putExtra(KEY_TYPE, type);
            setResult(Activity.RESULT_OK, data);
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void clickCancel() {
        finish();
    }
}

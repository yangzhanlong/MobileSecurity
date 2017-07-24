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
    public static final String KEY_POSITION = "potion";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";

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

        // 根据进入的行为来判断UI显示
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_ADD.equals(action)) {
            mTvTitle.setText("添加黑名单");
            mEtNumber.setEnabled(true);
            mBtnOk.setText("保存");
        } else {
            mTvTitle.setText("更新黑名单");
            mEtNumber.setEnabled(false);
            mEtNumber.setText(intent.getStringExtra(BlackEditActivity.KEY_NUMBER));
            int id = -1;
            switch (intent.getIntExtra(BlackEditActivity.KEY_TYPE, -1)) {
                case BlackBean.TYPE_CALL:
                    id = R.id.be_rb_call;
                    break;
                case BlackBean.TYPE_SMS:
                    id = R.id.be_rb_sms;
                    break;
                case BlackBean.TYPE_ALL:
                    id = R.id.be_rb_all;
                    break;
                default:
                    break;
            }
            mRgType.check(id);
            mBtnOk.setText("更新");
        }
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

        Intent intent = getIntent();
        String action = intent.getAction();
        int position = intent.getIntExtra(KEY_POSITION, -1);
        if (ACTION_ADD.equals(action)) {
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
        } else {
            boolean success = mDao.update(number, type);
            if (success) {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(KEY_POSITION, position);
                data.putExtra(KEY_TYPE, type);
                setResult(Activity.RESULT_OK, data);
            } else {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void clickCancel() {
        finish();
    }
}

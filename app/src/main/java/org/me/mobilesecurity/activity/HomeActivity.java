package org.me.mobilesecurity.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.HomeBean;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ImageView ivLogon;
    private GridView mGridView;
    private List<HomeBean> mDatas;

    private int[] ICONS = new int[]{
            R.drawable.sjfd, R.drawable.srlj,
            R.drawable.rjgj, R.drawable.jcgl,
            R.drawable.lltj, R.drawable.sjsd,
            R.drawable.hcql, R.drawable.cygj};

    private String[] TITLES = new String[] {
            "手机防盗", "骚扰拦截",
            "软件管家", "进程管理",
            "流量统计", "手机杀毒",
            "缓存清理", "常用工具"};

    private String[] DESCS = new String[] {
            "远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程",
            "流量一目了然", "病毒无处藏身",
            "系统快如火箭", "工具大全"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 初始化控件
        initView();

        // 让logo进行动画
        doLogoAnimation();

        // 给gridView加载数据
        initGridViewData();
    }

    private void initView() {
        ivLogon = (ImageView) findViewById(R.id.home_iv_logo);
        mGridView = (GridView) findViewById(R.id.home_gridview);
        mGridView.setOnItemClickListener(this);
    }

    private void initGridViewData() {
        // 给list数据初始化
        mDatas = new ArrayList<>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean bean = new HomeBean();
            bean.setIcon(ICONS[i]);
            bean.setTitle(TITLES[i]);
            bean.setDesc(DESCS[i]);
            mDatas.add(bean);
        }

        // gridview 和listView数据加载方式相似
        // 给gridview设置Adapter
        // Adapter的设计模式
        // 1. 实现adapter
        // 2. adapter 需要依赖List<T>数据源
        // 3. 确定List中item的数据类型
        // 4. item条目的UI显示---> bean
        mGridView.setAdapter(new HomeAdapter());
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
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                clickSjfd();
                break;
            case 1:
                clickSrlj();
                break;
        }
    }

    // 点击手机防盗
    private void clickSjfd() {
        // 检查是否有存储过密码
        String pwd = PreferenceUtils.getString(this, Config.KEY_SJFD_PWD, null);

        if (!TextUtils.isEmpty(pwd)) {
            // 如果有设置过密码，进入输入密码框
            showPwdEnterDialog();
        } else {
            // 如果没有设置过密码，进入设置密码框
            showPwdSettingDialog();
        }
    }

    // 设置密码对话框
    private void showPwdSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_pwd_setting, null);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
        final EditText etConfirm = (EditText) view.findViewById(R.id.dialog_et_confirm);

        Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

        // 取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 确定
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, R.string.p_input_pwd, Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus();
                    return;
                }

                String confirm = etConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(confirm)) {
                    Toast.makeText(HomeActivity.this, R.string.p_input_confirm_pwd, Toast.LENGTH_SHORT).show();
                    etConfirm.requestFocus();
                    return;
                }

                if (!pwd.trim().equals(confirm.trim())) {
                    Toast.makeText(HomeActivity.this, R.string.pwd_not_same, Toast.LENGTH_SHORT).show();
                    return;
                }

                PreferenceUtils.setString(HomeActivity.this, Config.KEY_SJFD_PWD, pwd.trim());
                dialog.dismiss();

                // 进入设置向导界面
                enterSetup1();
            }
        });
    }

    private void enterSetup1() {
        Intent intent = new Intent(this, SetupActivity1.class);
        startActivity(intent);
    }

    // 密码输入框
    private void showPwdEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);

        Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

        // 取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 确定
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断密码是否为空
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, R.string.p_input_pwd, Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus();
                    return;
                }

                // 判断密码是否正确
                String save_pwd = PreferenceUtils.getString(HomeActivity.this, Config.KEY_SJFD_PWD);
                if (!pwd.trim().equals(save_pwd)) {
                    Toast.makeText(HomeActivity.this, R.string.error_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
                // 判断是否设置过设置向导
                boolean flag = PreferenceUtils.getBoolean(HomeActivity.this, Config.KEY_SJFD_SETUP);
                if (flag) {
                    // 进入手机防盗界面
                    Intent intent = new Intent(HomeActivity.this, SjfdActivity.class);
                    startActivity(intent);
                } else {
                    // 进入设置向导界面
                    enterSetup1();
                }
            }
        });
    }

    // 骚扰拦截
    private void clickSrlj() {
        Intent intent = new Intent(this, CallSmsSafeActivity.class);
        startActivity(intent);
    }

    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);

            HomeBean bean = (HomeBean) getItem(position);
            ImageView ivIcon = (ImageView) convertView.findViewById(R.id.item_home_iv_icon);
            TextView tvTitel = (TextView) convertView.findViewById(R.id.item_home_tv_title);
            TextView tvDesc = (TextView) convertView.findViewById(R.id.item_home_tv_desc);

            // 给控件设置数据
            ivIcon.setImageResource(bean.getIcon());
            tvTitel.setText(bean.getTitle());
            tvDesc.setText(bean.getDesc());

            return convertView;
        }
    }
}

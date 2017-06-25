package org.me.mobilesecurity.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.HomeBean;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

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

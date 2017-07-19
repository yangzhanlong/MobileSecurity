package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.BlackBean;
import org.me.mobilesecurity.db.BlackDao;

import java.util.List;

public class CallSmsSafeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 100;
    private ListView mListView;
    private ImageView mIv;
    private List<BlackBean> mDatas;
    private BlackDao mDao;
    private CallSmsSafeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        // 创建dao对象
        mDao = new BlackDao(this);
        mDao.add("123", 1);

        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        mIv.setOnClickListener(this);
    }

    private void initView() {
        mIv = (ImageView) findViewById(R.id.css_iv_add);
        mListView = (ListView) findViewById(R.id.css_listview);
    }

    private void initData() {
        // 查询所有数据
        mDatas = mDao.findAll();
        // 实现adapter--》List<数据>-->item条目
        mAdapter = new CallSmsSafeAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.css_iv_add:
                clickAdd();
                break;
        }
    }

    private void clickAdd() {
        Intent intent = new Intent(this, BlackEditActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 添加的请求返回的数据
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // 获取数据
                String number = data.getStringExtra(BlackEditActivity.KEY_NUMBER);
                int type = data.getIntExtra(BlackEditActivity.KEY_TYPE, -1);

                BlackBean bean = new BlackBean();
                bean.setNumber(number);
                bean.setType(type);
                // 修改list数据
                mDatas.add(bean);
                // 刷新adapter
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class CallSmsSafeAdapter extends BaseAdapter{

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
            ViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                // 记载布局
                convertView = View.inflate(CallSmsSafeActivity.this, R.layout.item_callsmssafe, null);
                // 创建holder
                holder = new ViewHolder();
                // 设置标记
                convertView.setTag(holder);
                // 给holder中的view  findViewById
                holder.tvNumber = (TextView) convertView.findViewById(R.id.item_css_tv_number);
                holder.tvType = (TextView) convertView.findViewById(R.id.item_css_tv_type);
                holder.ivDelete = (ImageView) findViewById(R.id.item_css_iv_delete);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BlackBean bean = mDatas.get(position);
            holder.tvNumber.setText(bean.getNumber());
            switch (bean.getType()) {
                case BlackBean.TYPE_CALL:
                    holder.tvType.setText("电话拦截");
                    break;
                case BlackBean.TYPE_SMS:
                    holder.tvType.setText("短信拦截");
                    break;
                case BlackBean.TYPE_ALL:
                    holder.tvType.setText("电话+短信拦截");
                    break;
                default:
                    break;
            }

            return convertView;
        }
    }

    private static class ViewHolder{
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }
}

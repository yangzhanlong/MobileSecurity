package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.BlackBean;
import org.me.mobilesecurity.db.BlackDao;

import java.util.List;

public class CallSmsSafeActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final int REQUEST_ADD = 100;
    private static final int REQUEST_UPDATE = 200;
    private ListView mListView;
    private ImageView mIv, mIvEmpty;
    private List<BlackBean> mDatas;
    private BlackDao mDao;
    private CallSmsSafeAdapter mAdapter;
    private LinearLayout mLoading;
    private int mPageSize = 20;

    private boolean isLoadingMore;// 用来标记是否是正在加载更多
    private boolean isLoadAll;// 用来标记是否是加载所有数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        // 创建dao对象
        mDao = new BlackDao(this);

        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        mIv.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlackBean bean = mDatas.get(position);
        Intent intent = new Intent(this, BlackEditActivity.class);
        intent.setAction(BlackEditActivity.ACTION_UPDATE);
        intent.putExtra(BlackEditActivity.KEY_NUMBER, bean.getNumber());
        intent.putExtra(BlackEditActivity.KEY_TYPE, bean.getType());
        intent.putExtra(BlackEditActivity.KEY_POSITION, position);
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    private void initView() {
        mIv = (ImageView) findViewById(R.id.css_iv_add);
        mListView = (ListView) findViewById(R.id.css_listview);
        mLoading = (LinearLayout) findViewById(R.id.css_ll_loading);
        mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);
        mLoading = (LinearLayout) findViewById(R.id.css_ll_loading);
    }

    private void initData() {
        // 在耗时操作前，显示加载的进度
        mLoading.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询所有数据
                // mDatas = mDao.findAll();

                // 查询部分
                mDatas = mDao.findPart(mPageSize, 0);

                // 模拟延时操作
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // UI操作，需要回归主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 耗时操作后，隐藏进度
                        mLoading.setVisibility(View.GONE);
                        // 实现adapter--》List<数据>-->item条目
                        mAdapter = new CallSmsSafeAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
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
        intent.setAction(BlackEditActivity.ACTION_ADD);
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 添加的请求返回的数据
        if (requestCode == REQUEST_ADD) {
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
        } else if (requestCode == REQUEST_UPDATE) {
            if (resultCode == Activity.RESULT_OK) {
                // 获取数据
                int position = data.getIntExtra(BlackEditActivity.KEY_POSITION, -1);
                int type = data.getIntExtra(BlackEditActivity.KEY_TYPE, -1);

                // 更新list数据
                BlackBean bean = mDatas.get(position);
                bean.setType(type);

                // 刷新adapter
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    // 当滑动的状态发生改变是调用
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    // 当滑动时调用
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem:第一个可见的条目的position
        // visibleItemCount：可见条目的数量
        // totalItemCount:总共的条目数量
        if (mAdapter == null || mDatas == null) {
            return;
        }

        // 获得最后一个可见的条目
        int lastVisiblePosition = mListView.getLastVisiblePosition();

        // 如果滑动到底部
        if (lastVisiblePosition == mAdapter.getCount() - 1) {
            // 如果正在加载更多，不继续执行
            if (isLoadingMore) {
                return;
            }

            // 如果已经加载完成，不继续加载
            if (isLoadAll) {
                return;
            }

            // 开始加载更多了
            Log.d("CallSmsSafeActivity", "开始加载更多了");
            isLoadingMore = true;
            mLoading.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 加载更多的数据
                    final List<BlackBean> part = mDao.findPart(mPageSize,
                            mAdapter.getCount());

                    if (part == null || part.size() < mPageSize) {
                        isLoadAll = true;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoading.setVisibility(View.GONE);
                            // 添加部分
                            mDatas.addAll(part);
                            // UI更新
                            mAdapter.notifyDataSetChanged();
                            // 加载更多结束
                            isLoadingMore = false;
                        }
                    });
                }
            }).start();

        }

    }

    private class CallSmsSafeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (mDatas != null) {
                // 提示的空view
                mIvEmpty.setVisibility(mDatas.size() == 0 ? View.VISIBLE : View.GONE);
                return mDatas.size();
            }
            // 提示的空view可见
            mIvEmpty.setVisibility(View.VISIBLE);
            return 0;
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
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.item_css_iv_delete);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BlackBean bean = mDatas.get(position);
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

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 数据库删除
                    boolean success = mDao.delete(bean.getNumber());
                    if (success) {
                        // 删除数据(内存移除)
                        mDatas.remove(bean);

                        // 更新adapter
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSmsSafeActivity.this, "删除失败",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return convertView;
        }
    }

    private static class ViewHolder{
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }

}

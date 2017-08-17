package org.me.mobilesecurity.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.TrafficBean;
import org.me.mobilesecurity.business.TrafficProvider;

import java.util.List;

public class TrafficActivity extends AppCompatActivity {
    private ListView mListView;
    private TrafficAdapter mAdapter;
    private View mLoadingView;
    private List<TrafficBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.traffic_listview);
        mLoadingView = findViewById(R.id.include_ll_loading);
    }

    // 异步加载数据
    private void initData() {
        new AsyncTask<Void, Void, List<TrafficBean>>() {
            @Override
            protected void onPreExecute() {
                mLoadingView.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected List<TrafficBean> doInBackground(Void... params) {
                mDatas = TrafficProvider.getTraffics(TrafficActivity.this);
                return mDatas;
            }

            @Override
            protected void onPostExecute(List<TrafficBean> datas) {
                super.onPostExecute(datas);
                mLoadingView.setVisibility(View.GONE);
                mAdapter = new TrafficAdapter();
                mListView.setAdapter(mAdapter);
            }
        }.execute();
    }

    private class TrafficAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(TrafficActivity.this, R.layout.item_traffic, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_traffic_iv_icon);
                holder.name = (TextView) convertView.findViewById(R.id.item_traffic_tv_name);
                holder.receive = (TextView) convertView.findViewById(R.id.item_traffic_tv_receive);
                holder.send = (TextView) convertView.findViewById(R.id.item_traffic_tv_send);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TrafficBean bean = mDatas.get(position);
            if (bean.icon == null) {
                holder.icon.setImageResource(R.drawable.ic_default);
            } else {
                holder.icon.setImageDrawable(bean.icon);
            }
            holder.name.setText(bean.name);
            holder.receive.setText("接收:"
                    + Formatter.formatFileSize(TrafficActivity.this,
                    bean.receive));
            holder.send.setText("发送:"
                            + Formatter.formatFileSize(TrafficActivity.this,
                            bean.send));

            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView receive;
        public TextView send;
    }
}

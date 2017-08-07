package org.me.mobilesecurity.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.AddressStyleBean;
import org.me.mobilesecurity.utils.Config;
import org.me.mobilesecurity.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;


public class AddressStyleDialog extends Dialog implements AdapterView.OnItemClickListener{
    private int mScreenWidthPixels;
    private ListView mListView;
    private List<AddressStyleBean> mDatas;

    private static final String[] titles = new String[] { "半透明", "活力橙", "卫士蓝","金属灰", "苹果绿" };
    private static final int[] styles = new int[] { R.drawable.toast_normal,
            R.drawable.toast_orange, R.drawable.toast_blue,
            R.drawable.toast_gray, R.drawable.toast_green };

    public AddressStyleDialog(Context context) {
        super(context, R.style.addressStyle);
        // 获取屏幕的宽度，保证大屏幕可以显示完整
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mScreenWidthPixels = metrics.widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 去除默认的 title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置dialog的布局
        setContentView(R.layout.dialog_address_style);

        Window window = getWindow();
        LayoutParams params = window.getAttributes();
        // dialog 在底部居中显示
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        // 设置宽度为屏幕的宽度
        params.width = mScreenWidthPixels;
        window.setAttributes(params);
        
        initView();
        initData();
        initEvent();
        
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.dialog_listview);
    }

    private void initData() {
        int style = PreferenceUtils.getInt(getContext(),
                Config.KEY_ADDRESS_STYLE, -1);

        // 初始化mdatas
        mDatas = new ArrayList<AddressStyleBean>();
        for (int i = 0; i < titles.length; i++) {
            AddressStyleBean bean = new AddressStyleBean();
            bean.style = styles[i];
            bean.title = titles[i];
            if (style == -1) {
                // 一个也没有选中
                style = styles[0];
            }
            bean.selected = style == styles[i] ? true : false;

            mDatas.add(bean);
        }

        mListView.setAdapter(new AddressStyleAdapter());
    }

    private class AddressStyleAdapter extends BaseAdapter {

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
                // 没有复用
                // 1。加载
                convertView = View.inflate(getContext(),
                        R.layout.item_address_style, null);
                // 2. 新建holder
                holder = new ViewHolder();
                // 3.tag
                convertView.setTag(holder);
                // 4.findviewbyid
                holder.ivSelected = (ImageView) convertView
                        .findViewById(R.id.item_address_iv_selected);
                holder.ivStyle = (ImageView) convertView
                        .findViewById(R.id.item_address_iv_style);
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.item_address_tv_title);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            AddressStyleBean bean = mDatas.get(position);
            holder.ivStyle.setImageResource(bean.style);
            holder.ivSelected.setVisibility(bean.selected ? View.VISIBLE
                    : View.GONE);
            holder.tvTitle.setText(bean.title);

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView ivStyle;
        TextView tvTitle;
        ImageView ivSelected;
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击条目时的事件
        // dialog销毁
        dismiss();

        AddressStyleBean bean = mDatas.get(position);
        int style = bean.style;
        // 持久化存储选中项
        PreferenceUtils.setInt(getContext(), Config.KEY_ADDRESS_STYLE, style);
    }
}

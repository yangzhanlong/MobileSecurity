package org.me.mobilesecurity.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.ContactBean;
import org.me.mobilesecurity.business.ContactProvider;

import java.util.List;

import static org.me.mobilesecurity.business.ContactProvider.getContactPhoto;

public class ContactSelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final int REQUEST_CODE = 100;
    private static final String KEY_NUMBER = "number";
    private ListView mListView;
    private List<ContactBean> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);

        initView();

        // 加载数据
        initData();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.cs_listview);
    }

    private void initData() {
        // 如果没有权限，申请访问通讯录权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                // 申请授权。
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,}, REQUEST_CODE);
            }
        } else {
            // 初始化数据
            mDatas = ContactProvider.getAllContacts(this);
            // 设置adapter -> List<T> 数据源 -> item条目的ui
            mListView.setAdapter(new ContactSelectAdapter());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权，初始化数据
                    mDatas = ContactProvider.getAllContacts(this);
                    mListView.setAdapter(new ContactSelectAdapter());
                } else {
                    Toast.makeText(this, R.string.deny_perm, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击item, 获取手机号码，返回数据回去
        ContactBean bean = mDatas.get(position);
        Intent intent = new Intent();
        intent.putExtra(KEY_NUMBER, bean.getNumber());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class ContactSelectAdapter extends BaseAdapter {

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
            if (convertView == null) { // 没有复用
                // 加载布局
                convertView = View.inflate(ContactSelectActivity.this, R.layout.item_contact, null);

                // 创建holder
                holder = new ViewHolder();

                // findViewByid
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_contact_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_contact_tv_name);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.item_contact_tv_number);

                // 给convertView设置标识
                convertView.setTag(holder);

            } else { // 有复用，获取holder
                holder = (ViewHolder) convertView.getTag();
            }

            ContactBean bean = mDatas.get(position);
            holder.tvName.setText(bean.getName());
            holder.tvNumber.setText(bean.getNumber());
            holder.ivIcon.setImageBitmap(getContactPhoto(ContactSelectActivity.this, bean.getContactId()));

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvNumber;
    }
}
